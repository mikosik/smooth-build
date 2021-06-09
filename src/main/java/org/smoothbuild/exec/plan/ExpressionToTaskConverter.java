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
import org.smoothbuild.exec.nativ.NativeImplLoader;
import org.smoothbuild.install.FullPathResolver;
import org.smoothbuild.lang.base.define.Callable;
import org.smoothbuild.lang.base.define.Constructor;
import org.smoothbuild.lang.base.define.DefinedBody;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.Function;
import org.smoothbuild.lang.base.define.ImplementedBy;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.ModuleLocation;
import org.smoothbuild.lang.base.define.NativeBody;
import org.smoothbuild.lang.base.define.Referencable;
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
import org.smoothbuild.lang.expr.ExpressionVisitorException;
import org.smoothbuild.lang.expr.FieldReadExpression;
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
  private final NativeImplLoader nativeImplLoader;
  private final FullPathResolver pathResolver;

  @Inject
  public ExpressionToTaskConverter(Definitions definitions, ObjectFactory objectFactory,
      NativeImplLoader nativeImplLoader, FullPathResolver pathResolver) {
    this.toSpecConverter = new TypeToSpecConverter(objectFactory);
    this.definitions = definitions;
    this.nativeImplLoader = nativeImplLoader;
    this.pathResolver = pathResolver;
  }

  @Override
  public Task visit(Scope<TaskSupplier> scope, FieldReadExpression expression)
      throws ExpressionVisitorException {
    ItemSignature field = expression.field();
    StructType structType = (StructType) expression.expression().type();
    Algorithm algorithm = new ReadTupleElementAlgorithm(
        structType.fieldIndex(field.name().get()), field.type().visit(toSpecConverter));
    List<Task> children = childrenTasks(scope, expression.expression());
    return new NormalTask(CALL, field.type(), "." + field.name().get(), algorithm, children,
        expression.location());
  }

  @Override
  public Task visit(Scope<TaskSupplier> scope, ReferenceExpression reference)
      throws ExpressionVisitorException {
    Value value = (Value) definitions.referencables().get(reference.name());
    if (value.body() instanceof DefinedBody body) {
      Task task = body.expression().visit(scope, this);
      Task convertedTask = convertIfNeeded(task, value.type());
      return new VirtualTask(value.extendedName(), convertedTask, VALUE, reference.location());
    } else {
      Task nativeCode = createNativeCodeTask((NativeBody) value.body(), value);
      boolean isPure = ((NativeBody) value.body()).implementedBy().isPure();
      Algorithm algorithm = new CallNativeAlgorithm(nativeImplLoader,
          value.type().visit(toSpecConverter), value, isPure);
      List<Task> dependencies = list(nativeCode);
      return new NormalTask(VALUE, value.type(), value.extendedName(), algorithm,
          dependencies, reference.location());
    }
  }

  @Override
  public Task visit(Scope<TaskSupplier> scope, ParameterReferenceExpression expression)
      throws ExpressionVisitorException {
    return scope.get(expression.name()).getTask();
  }

  @Override
  public Task visit(Scope<TaskSupplier> scope, CallExpression expression)
      throws ExpressionVisitorException {
    Callable callable = expression.callable();
    if (callable instanceof Function function) {
      var argumentTypes = map(expression.arguments(), e -> expressionType(scope, e));
      var variables = inferVariableBounds(function.type().parameterTypes(), argumentTypes, LOWER);
      Type actualResultType = function.resultType().mapVariables(variables, LOWER);

      if (function.body() instanceof DefinedBody) {
        var arguments = childrenTaskSuppliers(scope, expression.arguments(), argumentTypes);
        return taskForDefinedFunction(
            scope, actualResultType, function, arguments, expression.location());
      } else {
        var arguments = childrenTasks(scope, expression.arguments());
        return taskForNativeFunction(arguments, function, variables, actualResultType,
            expression.location());
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
      Function function, List<TaskSupplier> arguments, Location location)
      throws ExpressionVisitorException {
    var newScope = new Scope<>(scope, nameToArgumentMap(function.parameters(), arguments));
    DefinedBody body = (DefinedBody) function.body();
    Task callTask = body.expression().visit(newScope, this);
    Task task = convertIfNeeded(callTask, actualResultType);
    return new VirtualTask(function.extendedName(), task, CALL, location);
  }

  private Task taskForNativeFunction(List<Task> arguments, Function function,
      BoundedVariables variables, Type actualResultType, Location location) {
    Task nativeCode = createNativeCodeTask((NativeBody) function.body(), function);
    boolean isPure = ((NativeBody) function.body()).implementedBy().isPure();
    Algorithm algorithm = new CallNativeAlgorithm(nativeImplLoader,
        actualResultType.visit(toSpecConverter), function, isPure);
    ImmutableList<Type> actualParameterTypes =
        map(function.type().parameterTypes(), t -> t.mapVariables(variables, LOWER));
    List<Task> functionArgTasks = convertedArguments(actualParameterTypes, arguments);
    List<Task> dependencies = ImmutableList.<Task>builder()
        .add(nativeCode)
        .addAll(functionArgTasks)
        .build();
    if (function.name().equals(IF_FUNCTION_NAME)) {
      return new IfTask(actualResultType, algorithm, dependencies, location);
    } else {
      return new NormalTask(CALL, actualResultType, function.extendedName(), algorithm,
          dependencies, location);
    }
  }

  private Task createNativeCodeTask(NativeBody body, Referencable referencable) {
    ModuleLocation module = referencable.location().moduleLocation().toNative();
    var contentAlgorithm = new ReadFileContentAlgorithm(
        toSpecConverter.visit(blob()), module, nativeImplLoader, pathResolver);

    ImplementedBy implementedBy = body.implementedBy();
    String name = "_native_module('" + module.prefixedPath() + "')";
    var contentTask = new NormalTask(
        NATIVE, blob(), name, contentAlgorithm, list(), implementedBy.location());
    var methodPathTask = fixedStringTask(implementedBy.path(), implementedBy.location());

    return taskForConstructorCall(
        NATIVE, referencable.type(), toSpecConverter.nativeCodeSpec(), "_native_function",
        list(methodPathTask, contentTask), referencable.location()
    );
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
  public Task visit(Scope<TaskSupplier> scope, ArrayLiteralExpression expression)
      throws ExpressionVisitorException {
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
    return new NormalTask(LITERAL, blob(), algorithm.shortedLiteral(), algorithm,
        ImmutableList.of(), expression.location());
  }

  @Override
  public Task visit(Scope<TaskSupplier> scope, StringLiteralExpression expression) {
    return fixedStringTask(expression.string(), expression.location());
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

  private List<Task> childrenTasks(Scope<TaskSupplier> scope, List<Expression> children)
      throws ExpressionVisitorException {
    ImmutableList.Builder<Task> builder = ImmutableList.builder();
    for (Expression child : children) {
      builder.add(child.visit(scope, this));
    }
    return builder.build();
  }

  private ImmutableList<Task> childrenTasks(Scope<TaskSupplier> scope, Expression expression)
      throws ExpressionVisitorException {
    return ImmutableList.of(expression.visit(scope, this));
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
