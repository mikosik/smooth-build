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
import org.smoothbuild.io.fs.space.FilePath;
import org.smoothbuild.io.fs.space.FileResolver;
import org.smoothbuild.lang.base.define.Callable;
import org.smoothbuild.lang.base.define.Constructor;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.Function;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.Value;
import org.smoothbuild.lang.base.type.ArrayType;
import org.smoothbuild.lang.base.type.BoundedVariables;
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

public class ExpressionToTaskConverter
    implements ExpressionVisitor<Scope<TaskSupplier>, TaskSupplier> {
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
  public TaskSupplier visit(Scope<TaskSupplier> scope, FieldReadExpression expression) {
    Type type = expression.field().type();
    return new TaskSupplier(type, expression.location(), () -> {
      StructType structType = (StructType) expression.expression().type();
      String name = expression.field().name().get();
      Algorithm algorithm = new ReadTupleElementAlgorithm(
          structType.fieldIndex(name), type.visit(toSpecConverter));
      List<TaskSupplier> children = childrenTasks(scope, expression.expression());
      return new NormalTask(CALL, type, "." + name, algorithm, children, expression.location());
    });
  }

  @Override
  public TaskSupplier visit(Scope<TaskSupplier> scope, ReferenceExpression reference) {
    Value value = (Value) definitions.referencables().get(reference.name());
    if (value.body() instanceof NativeExpression nativ) {
      return new TaskSupplier(value.type(), reference.location(), () -> {
        Algorithm algorithm = new CallNativeAlgorithm(
            javaCodeLoader, value.type().visit(toSpecConverter), value, nativ.isPure());
        TaskSupplier nativeCode = visit(scope, nativ);
        return new NormalTask(VALUE, value.type(), value.extendedName(), algorithm,
            list(nativeCode), reference.location());
      });
    } else {
      return new TaskSupplier(value.type(), reference.location(), () -> {
        TaskSupplier taskSupplier = convertIfNeeded(value.body().visit(scope, this), value.type());
        return new VirtualTask(VALUE, value.extendedName(), taskSupplier, reference.location());
      });
    }
  }

  @Override
  public TaskSupplier visit(Scope<TaskSupplier> scope, ParameterReferenceExpression expression) {
    return scope.get(expression.name());
  }

  @Override
  public TaskSupplier visit(Scope<TaskSupplier> scope, CallExpression expression) {
    // TODO This cast is temporary as for now CallExpression is always created with
    //  ReferenceExpression
    String name = ((ReferenceExpression) expression.callable()).name();
    Callable callable = (Callable) definitions.referencables().get(name);
    if (callable instanceof Function function) {
      var argumentTypes = map(expression.arguments(), e -> expressionType(scope, e));
      var variables = inferVariableBounds(function.type().parameterTypes(), argumentTypes, LOWER);
      Type actualResultType = function.resultType().mapVariables(variables, LOWER);

      if (function.body() instanceof NativeExpression nativ) {
        return new TaskSupplier(actualResultType, expression.location(), () -> {
          var arguments = childrenTasks(scope, expression.arguments());
          return taskForNativeFunction(scope, arguments, function, nativ, variables,
              actualResultType, expression.location());
        });
      } else {
        return new TaskSupplier(actualResultType, expression.location(), () -> {
          var arguments = childrenTasks(scope, expression.arguments());
          return taskForDefinedFunction(
              scope, actualResultType, function, arguments, expression.location());
        });
      }
    } else if (callable instanceof Constructor constructor) {
      Type resultType = constructor.type().resultType();
      return new TaskSupplier(resultType, expression.location(), () -> {
        TupleSpec tupleSpec = (TupleSpec) resultType.visit(toSpecConverter);
        List<TaskSupplier> dependencies = childrenTasks(scope, expression.arguments());
        return taskForConstructorCall(CALL, resultType, tupleSpec, constructor.extendedName(),
            dependencies, expression.location());
      });
    } else {
      throw new RuntimeException("Unexpected case: " + callable.getClass().getCanonicalName());
    }
  }

  private Task taskForConstructorCall(TaskKind taskKind, Type resultType, TupleSpec tupleSpec,
      String name, List<TaskSupplier> dependencies, Location location) {
    Algorithm algorithm = new CreateTupleAlgorithm(tupleSpec);
    return new NormalTask(taskKind, resultType, name, algorithm, dependencies, location);
  }

  private Task taskForDefinedFunction(Scope<TaskSupplier> scope, Type actualResultType,
      Function function, List<TaskSupplier> arguments, Location location) {
    var newScope = new Scope<>(scope, nameToArgumentMap(function.parameters(), arguments));
    var taskSupplier = convertIfNeeded(function.body().visit(newScope, this), actualResultType);
    return new VirtualTask(CALL, function.extendedName(), taskSupplier, location);
  }

  private Task taskForNativeFunction(Scope<TaskSupplier> scope, List<TaskSupplier> arguments,
      Function function, NativeExpression nativ, BoundedVariables variables, Type actualResultType,
      Location location) {
    TaskSupplier nativeCode = visit(scope, nativ);
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

  private List<TaskSupplier> convertedArguments(
      List<Type> actualParameterTypes, List<TaskSupplier> arguments) {
    List<TaskSupplier> result = new ArrayList<>();
    for (int i = 0; i < arguments.size(); i++) {
      Type type = actualParameterTypes.get(i);
      result.add(convertIfNeeded(arguments.get(i), type));
    }
    return result;
  }

  @Override
  public TaskSupplier visit(Scope<TaskSupplier> scope, ArrayLiteralExpression expression) {
    List<TaskSupplier> elements = childrenTasks(scope, expression.elements());
    ArrayType actualType = arrayType(elements).orElse(expression.type());

    return new TaskSupplier(actualType, expression.location(), () -> {
      Algorithm algorithm = new CreateArrayAlgorithm(toSpecConverter.visit(actualType));
      var convertedElements = convertedElements(actualType.elemType(), elements);
      return new NormalTask(LITERAL, actualType, actualType.name(), algorithm, convertedElements,
          expression.location());
    });
  }

  private ImmutableList<TaskSupplier> convertedElements(Type type, List<TaskSupplier> elements) {
    return map(elements, t -> convertIfNeeded(t, type));
  }

  private Optional<ArrayType> arrayType(List<TaskSupplier> elements) {
    return elements
        .stream()
        .map(TaskSupplier::type)
        .reduce((typeA, typeB) -> typeA.mergeWith(typeB, UPPER))
        .map(Types::array);
  }

  @Override
  public TaskSupplier visit(Scope<TaskSupplier> scope, BlobLiteralExpression expression) {
    return new TaskSupplier(blob(), expression.location(), () -> {
      var blobSpec = toSpecConverter.visit(blob());
      var algorithm = new FixedBlobAlgorithm(blobSpec, expression.byteString());
      return new NormalTask(
          LITERAL, blob(), algorithm.shortedLiteral(), algorithm, list(), expression.location());
    });
  }

  @Override
  public TaskSupplier visit(Scope<TaskSupplier> scope, StringLiteralExpression expression) {
    return fixedStringTask(expression.string(), expression.location());
  }

  @Override
  public TaskSupplier visit(Scope<TaskSupplier> context, NativeExpression expression) {
    TaskSupplier contentTask = nativeBlobTask(expression);
    TaskSupplier methodPathTask = fixedStringTask(expression.path(), expression.location());
    return new TaskSupplier(expression.type(), expression.location(), () -> {
      ImmutableList<TaskSupplier> dependencies = list(methodPathTask, contentTask);
      return taskForConstructorCall(NATIVE, expression.type(), toSpecConverter.nativeCodeSpec(),
          "_native_function", dependencies, expression.location());
    });
  }

  private TaskSupplier nativeBlobTask(NativeExpression expression) {
    return new TaskSupplier(blob(), expression.location(), () -> {
      FilePath nativeFile = expression.nativeFile();
      var contentAlgorithm = new ReadFileContentAlgorithm(
          toSpecConverter.visit(blob()), nativeFile, javaCodeLoader, fileResolver);
      String name = "_native_module('" + nativeFile.toString() + "')";
      return new NormalTask(NATIVE, blob(), name, contentAlgorithm, list(), expression.location());
    });
  }

  private TaskSupplier fixedStringTask(String string, Location location) {
    return new TaskSupplier(string(), location, () -> {
      var stringType = toSpecConverter.visit(string());
      var algorithm = new FixedStringAlgorithm(stringType, string);
      String name = algorithm.shortedString();
      return new NormalTask(LITERAL, string(), name, algorithm, list(), location);
    });
  }

  private List<TaskSupplier> childrenTasks(Scope<TaskSupplier> scope, List<Expression> children) {
    ImmutableList.Builder<TaskSupplier> builder = ImmutableList.builder();
    for (Expression child : children) {
      builder.add(child.visit(scope, this));
    }
    return builder.build();
  }

  private ImmutableList<TaskSupplier> childrenTasks(Scope<TaskSupplier> scope,
      Expression expression) {
    return list(expression.visit(scope, this));
  }

  private TaskSupplier convertIfNeeded(TaskSupplier task, Type requiredType) {
    if (task.type().equals(requiredType)) {
      return task;
    } else {
      return convert(requiredType, task);
    }
  }

  private TaskSupplier convert(Type requiredType, TaskSupplier task) {
    return new TaskSupplier(requiredType, task.location(), () -> {
      String description = requiredType.name() + "<-" + task.type().name();
      Algorithm algorithm = new ConvertAlgorithm(requiredType.visit(toSpecConverter));
      List<TaskSupplier> dependencies = list(task);
      return new NormalTask(
          CONVERSION, requiredType, description, algorithm, dependencies, task.location());
    });
  }
}
