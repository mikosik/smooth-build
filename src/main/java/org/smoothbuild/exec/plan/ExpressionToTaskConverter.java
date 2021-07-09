package org.smoothbuild.exec.plan;

import static org.smoothbuild.exec.compute.IfTask.IF_FUNCTION_NAME;
import static org.smoothbuild.exec.compute.TaskKind.CALL;
import static org.smoothbuild.exec.compute.TaskKind.CONVERSION;
import static org.smoothbuild.exec.compute.TaskKind.LITERAL;
import static org.smoothbuild.exec.compute.TaskKind.NATIVE;
import static org.smoothbuild.exec.compute.TaskKind.VALUE;
import static org.smoothbuild.lang.base.type.Side.LOWER;
import static org.smoothbuild.lang.base.type.Side.UPPER;
import static org.smoothbuild.lang.base.type.Type.inferVariableBounds;
import static org.smoothbuild.lang.base.type.Types.blob;
import static org.smoothbuild.lang.base.type.Types.string;
import static org.smoothbuild.util.Lists.concat;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Lists.zip;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.db.object.spec.TupleSpec;
import org.smoothbuild.exec.algorithm.Algorithm;
import org.smoothbuild.exec.algorithm.CallNativeAlgorithm;
import org.smoothbuild.exec.algorithm.ConvertAlgorithm;
import org.smoothbuild.exec.algorithm.CreateArrayAlgorithm;
import org.smoothbuild.exec.algorithm.CreateTupleAlgorithm;
import org.smoothbuild.exec.algorithm.FixedBlobAlgorithm;
import org.smoothbuild.exec.algorithm.FixedStringAlgorithm;
import org.smoothbuild.exec.algorithm.ReadFileContentAlgorithm;
import org.smoothbuild.exec.algorithm.ReadTupleElementAlgorithm;
import org.smoothbuild.exec.compute.IfTask;
import org.smoothbuild.exec.compute.NormalTask;
import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.exec.compute.TaskKind;
import org.smoothbuild.exec.compute.VirtualTask;
import org.smoothbuild.exec.java.JavaCodeLoader;
import org.smoothbuild.io.fs.base.FilePath;
import org.smoothbuild.io.fs.base.FileResolver;
import org.smoothbuild.lang.base.define.Callable;
import org.smoothbuild.lang.base.define.Constructor;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.Function;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.Value;
import org.smoothbuild.lang.base.type.ArrayType;
import org.smoothbuild.lang.base.type.BoundedVariables;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.Types;
import org.smoothbuild.lang.expr.ArrayLiteralExpression;
import org.smoothbuild.lang.expr.BlobLiteralExpression;
import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.ExpressionVisitor;
import org.smoothbuild.lang.expr.FieldReadExpression;
import org.smoothbuild.lang.expr.NativeExpression;
import org.smoothbuild.lang.expr.ParameterReferenceExpression;
import org.smoothbuild.lang.expr.ReferenceExpression;
import org.smoothbuild.lang.expr.StringLiteralExpression;
import org.smoothbuild.util.Scope;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ExpressionToTaskConverter implements ExpressionVisitor<Scope<TaskSupplier>, Task> {
  private final Definitions definitions;
  private final TypeToSpecConverter toSpecConverter;
  private final JavaCodeLoader javaCodeLoader;
  private final FileResolver fileResolver;

  @Inject
  public ExpressionToTaskConverter(Definitions definitions, ObjectFactory objectFactory,
      JavaCodeLoader javaCodeLoader, FileResolver fileResolver) {
    this.toSpecConverter = new TypeToSpecConverter(objectFactory);
    this.definitions = definitions;
    this.javaCodeLoader = javaCodeLoader;
    this.fileResolver = fileResolver;
  }

  @Override
  public Task visit(Scope<TaskSupplier> scope, FieldReadExpression expression) {
    ItemSignature field = expression.field();
    StructType structType = (StructType) expression.expression().type();
    Algorithm algorithm = new ReadTupleElementAlgorithm(
        structType.fieldIndex(field.name().get()), field.type().visit(toSpecConverter));
    List<Task> children = childrenTasks(scope, expression.expression());
    return new NormalTask(CALL, field.type(), "." + field.name().get(), algorithm, children,
        expression.location());
  }

  @Override
  public Task visit(Scope<TaskSupplier> scope, ReferenceExpression reference) {
    Value value = (Value) definitions.referencables().get(reference.name());
    if (value.body() instanceof NativeExpression nativ) {
      Algorithm algorithm = new CallNativeAlgorithm(
          javaCodeLoader, value.type().visit(toSpecConverter), value, nativ.isPure());
      Task nativeCode = visit(scope, nativ);
      return new NormalTask(VALUE, value.type(), value.extendedName(), algorithm,
          list(nativeCode), reference.location());
    } else {
      Task task = value.body().visit(scope, this);
      Task convertedTask = convertIfNeeded(task, value.type());
      return new VirtualTask(VALUE, value.extendedName(), convertedTask, reference.location());
    }
  }

  @Override
  public Task visit(Scope<TaskSupplier> scope, ParameterReferenceExpression expression) {
    return scope.get(expression.name()).getTask();
  }

  @Override
  public Task visit(Scope<TaskSupplier> scope, CallExpression expression) {
    // TODO This cast is temporary as for now CallExpression is always created with
    //  ReferenceExpression
    String name = ((ReferenceExpression) expression.callable()).name();
    Callable callable = (Callable) definitions.referencables().get(name);
    if (callable instanceof Function function) {
      var argumentTypes = map(expression.arguments(), e -> expressionType(scope, e));
      var variables = inferVariableBounds(function.type().parameterTypes(), argumentTypes, LOWER);
      Type actualResultType = function.resultType().mapVariables(variables, LOWER);

      if (function.body() instanceof NativeExpression nativ) {
        var arguments = childrenTasks(scope, expression.arguments());
        return taskForNativeFunction(scope, arguments, function, nativ, variables, actualResultType,
            expression.location());
      } else {
        var arguments = childrenTaskSuppliers(scope, expression.arguments(), argumentTypes);
        return taskForDefinedFunction(
            scope, actualResultType, function, arguments, expression.location());
      }
    } else if (callable instanceof Constructor constructor) {
      Type resultType = constructor.type().resultType();
      TupleSpec tupleSpec = (TupleSpec) resultType.visit(toSpecConverter);
      List<Task> dependencies = childrenTasks(scope, expression.arguments());
      return taskForConstructorCall(CALL, resultType, tupleSpec, constructor.extendedName(),
          dependencies, expression.location());
    } else {
      throw new RuntimeException("Unexpected case: " + callable.getClass().getCanonicalName());
    }
  }

  private Task taskForConstructorCall(TaskKind taskKind, Type resultType, TupleSpec tupleSpec,
      String name, List<Task> dependencies, Location location) {
    Algorithm algorithm = new CreateTupleAlgorithm(tupleSpec);
    return new NormalTask(taskKind, resultType, name, algorithm, dependencies, location);
  }

  private Task taskForDefinedFunction(Scope<TaskSupplier> scope, Type actualResultType,
      Function function, List<TaskSupplier> arguments, Location location) {
    var newScope = new Scope<>(scope, nameToArgumentMap(function.parameters(), arguments));
    Task callTask = function.body().visit(newScope, this);
    Task task = convertIfNeeded(callTask, actualResultType);
    return new VirtualTask(CALL, function.extendedName(), task, location);
  }

  private Task taskForNativeFunction(Scope<TaskSupplier> scope, List<Task> arguments,
      Function function, NativeExpression nativ, BoundedVariables variables, Type actualResultType,
      Location location) {
    Task nativeCode = visit(scope, nativ);
    Algorithm algorithm = new CallNativeAlgorithm(
        javaCodeLoader, actualResultType.visit(toSpecConverter), function, nativ.isPure());
    var actualParameterTypes = map(
        function.type().parameterTypes(), t -> t.mapVariables(variables, LOWER));
    var dependencies = concat(nativeCode, convertedArguments(actualParameterTypes, arguments));
    if (function.name().equals(IF_FUNCTION_NAME)) {
      return new IfTask(actualResultType, algorithm, dependencies, location);
    } else {
      return new NormalTask(CALL, actualResultType, function.extendedName(), algorithm,
          dependencies, location);
    }
  }

  private static ImmutableMap<String, TaskSupplier> nameToArgumentMap(
      List<Item> names, List<TaskSupplier> arguments) {
    Builder<String, TaskSupplier> builder = ImmutableMap.builder();
    for (int i = 0; i < arguments.size(); i++) {
      builder.put(names.get(i).name(), arguments.get(i));
    }
    return builder.build();
  }

  private static Type expressionType(Scope<TaskSupplier> scope, Expression expression) {
    if (expression instanceof ParameterReferenceExpression parameterReference) {
      return scope.get(parameterReference.name()).type();
    } else {
      return expression.type();
    }
  }

  private List<Task> convertedArguments(List<Type> actualParameterTypes, List<Task> arguments) {
    List<Task> result = new ArrayList<>();
    for (int i = 0; i < arguments.size(); i++) {
      Type type = actualParameterTypes.get(i);
      result.add(convertIfNeeded(arguments.get(i), type));
    }
    return result;
  }

  @Override
  public Task visit(Scope<TaskSupplier> scope, ArrayLiteralExpression expression) {
    List<Task> elements = childrenTasks(scope, expression.elements());
    ArrayType actualType = arrayType(elements).orElse(expression.type());

    Algorithm algorithm = new CreateArrayAlgorithm(toSpecConverter.visit(actualType));
    List<Task> convertedElements = convertedElements(actualType.elemType(), elements);
    return new NormalTask(LITERAL, actualType, actualType.name(), algorithm, convertedElements,
        expression.location());
  }

  private List<Task> convertedElements(Type type, List<Task> elements) {
    return map(elements, t -> convertIfNeeded(t, type));
  }

  private Optional<ArrayType> arrayType(List<Task> elements) {
    return elements
        .stream()
        .map(Task::type)
        .reduce((typeA, typeB) -> typeA.mergeWith(typeB, UPPER))
        .map(Types::array);
  }

  @Override
  public Task visit(Scope<TaskSupplier> scope, BlobLiteralExpression expression) {
    var blobSpec = toSpecConverter.visit(blob());
    var algorithm = new FixedBlobAlgorithm(blobSpec, expression.byteString());
    return new NormalTask(
        LITERAL, blob(), algorithm.shortedLiteral(), algorithm, list(), expression.location());
  }

  @Override
  public Task visit(Scope<TaskSupplier> scope, StringLiteralExpression expression) {
    return fixedStringTask(expression.string(), expression.location());
  }

  @Override
  public Task visit(Scope<TaskSupplier> context, NativeExpression expression) {
    FilePath nativeFile = expression.nativeFile();
    var contentAlgorithm = new ReadFileContentAlgorithm(
        toSpecConverter.visit(blob()), nativeFile, javaCodeLoader, fileResolver);

    String name = "_native_module('" + nativeFile.toString() + "')";
    var contentTask = new NormalTask(
        NATIVE, blob(), name, contentAlgorithm, list(), expression.location());
    var methodPathTask = fixedStringTask(expression.path(), expression.location());

    return taskForConstructorCall(
        NATIVE, expression.type(), toSpecConverter.nativeCodeSpec(), "_native_function",
        list(methodPathTask, contentTask), expression.location());
  }

  private NormalTask fixedStringTask(String string, Location location) {
    var stringType = toSpecConverter.visit(string());
    var algorithm = new FixedStringAlgorithm(stringType, string);
    String name = algorithm.shortedString();
    return new NormalTask(LITERAL, string(), name, algorithm, list(), location);
  }

  private ImmutableList<TaskSupplier> childrenTaskSuppliers(Scope<TaskSupplier> scope,
      ImmutableList<Expression> expressions, ImmutableList<Type> argumentTypes) {
    return zip(expressions, argumentTypes,
        (e, t) -> new TaskSupplier(t, () -> e.visit(scope, this)));
  }

  private List<Task> childrenTasks(Scope<TaskSupplier> scope, List<Expression> children) {
    ImmutableList.Builder<Task> builder = ImmutableList.builder();
    for (Expression child : children) {
      builder.add(child.visit(scope, this));
    }
    return builder.build();
  }

  private ImmutableList<Task> childrenTasks(Scope<TaskSupplier> scope, Expression expression) {
    return list(expression.visit(scope, this));
  }

  private Task convertIfNeeded(Task task, Type requiredType) {
    if (task.type().equals(requiredType)) {
      return task;
    } else {
      return convert(requiredType, task);
    }
  }

  private NormalTask convert(Type requiredType, Task task) {
    String description = requiredType.name() + "<-" + task.type().name();
    Algorithm algorithm = new ConvertAlgorithm(requiredType.visit(toSpecConverter));
    List<Task> dependencies = list(task);
    return new NormalTask(
        CONVERSION, requiredType, description, algorithm, dependencies, task.location());
  }
}
