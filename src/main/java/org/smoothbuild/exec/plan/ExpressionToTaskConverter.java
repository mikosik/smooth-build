package org.smoothbuild.exec.plan;

import static org.smoothbuild.exec.compute.IfTask.IF_FUNCTION_NAME;
import static org.smoothbuild.exec.compute.TaskKind.CALL;
import static org.smoothbuild.exec.compute.TaskKind.CONVERSION;
import static org.smoothbuild.exec.compute.TaskKind.FUNCTION_REFERENCE;
import static org.smoothbuild.exec.compute.TaskKind.LITERAL;
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

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.db.object.spec.TupleSpec;
import org.smoothbuild.exec.algorithm.CallNativeAlgorithm;
import org.smoothbuild.exec.algorithm.ConvertAlgorithm;
import org.smoothbuild.exec.algorithm.CreateArrayAlgorithm;
import org.smoothbuild.exec.algorithm.CreateTupleAlgorithm;
import org.smoothbuild.exec.algorithm.FixedBlobAlgorithm;
import org.smoothbuild.exec.algorithm.FixedStringAlgorithm;
import org.smoothbuild.exec.algorithm.FunctionReferenceAlgorithm;
import org.smoothbuild.exec.algorithm.ReadTupleElementAlgorithm;
import org.smoothbuild.exec.compute.AlgorithmTask;
import org.smoothbuild.exec.compute.CallTask;
import org.smoothbuild.exec.compute.DefaultValueTask;
import org.smoothbuild.exec.compute.IfTask;
import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.exec.compute.TaskKind;
import org.smoothbuild.exec.compute.VirtualTask;
import org.smoothbuild.exec.java.MethodLoader;
import org.smoothbuild.lang.base.define.Constructor;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.Function;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.RealFunction;
import org.smoothbuild.lang.base.define.Value;
import org.smoothbuild.lang.base.type.ArrayType;
import org.smoothbuild.lang.base.type.BoundedVariables;
import org.smoothbuild.lang.base.type.FunctionType;
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

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ExpressionToTaskConverter
    implements ExpressionVisitor<Scope<TaskSupplier>, TaskSupplier> {
  private final Definitions definitions;
  private final TypeToSpecConverter toSpecConverter;
  private final MethodLoader methodLoader;

  @Inject
  public ExpressionToTaskConverter(Definitions definitions, ObjectFactory objectFactory,
      MethodLoader methodLoader) {
    this.toSpecConverter = new TypeToSpecConverter(objectFactory);
    this.definitions = definitions;
    this.methodLoader = methodLoader;
  }

  @Override
  public TaskSupplier visit(Scope<TaskSupplier> scope, FieldReadExpression expression) {
    var type = expression.field().type();
    return new TaskSupplier(type, expression.location(), () -> {
      var structType = (StructType) expression.expression().type();
      var name = expression.field().name().get();
      var algorithm = new ReadTupleElementAlgorithm(
          structType.fieldIndex(name), type.visit(toSpecConverter));
      var children = childrenTasks(scope, list(expression.expression()));
      return new AlgorithmTask(CALL, type, "." + name, algorithm, children, expression.location());
    });
  }

  @Override
  public TaskSupplier visit(Scope<TaskSupplier> scope, ReferenceExpression reference) {
    var referencable = definitions.referencables().get(reference.name());
    if (referencable instanceof Value value) {
      if (value.body() instanceof NativeExpression nativ) {
        return new TaskSupplier(value.type(), reference.location(), () -> {
          var algorithm = new CallNativeAlgorithm(methodLoader,
              value.type().visit(toSpecConverter), value, nativ.isPure());
          var nativeCode = visit(scope, nativ);
          return new AlgorithmTask(VALUE, value.type(), value.extendedName(), algorithm,
              list(nativeCode), reference.location());
        });
      } else {
        return new TaskSupplier(value.type(), reference.location(), () -> {
          var task = value.body().visit(scope, this);
          var convertedTask = convertIfNeeded(value.type(), task);
          return new VirtualTask(VALUE, value.extendedName(), convertedTask, reference.location());
        });
      }
    } else {
      var function = (Function) referencable;
      var type = function.type().strip();
      return new TaskSupplier(type, reference.location(), () -> {
        var module = definitions.modules().get(function.modulePath());
        var algorithm = new FunctionReferenceAlgorithm(
            function, module, toSpecConverter.functionSpec());
        return new AlgorithmTask(FUNCTION_REFERENCE, type, function.extendedName(),
            algorithm, list(), reference.location());
      });
    }
  }

  @Override
  public TaskSupplier visit(Scope<TaskSupplier> scope, ParameterReferenceExpression expression) {
    return scope.get(expression.name());
  }

  @Override
  public TaskSupplier visit(Scope<TaskSupplier> scope, CallExpression expression) {
    var location = expression.location();
    var function = expression.function().visit(scope, this);
    var arguments = argumentTasks(scope, function, expression.arguments());
    var argumentTypes = map(arguments, TaskSupplier::type);
    var functionType = (FunctionType) function.type();
    var variables = inferVariableBounds(functionType.parameterTypes(), argumentTypes, LOWER);
    var actualResultType = functionType.resultType().mapVariables(variables, LOWER);

    return new TaskSupplier(actualResultType, location, () -> new CallTask(CALL, actualResultType,
        "_function_call", concat(function, arguments), location, variables, scope, this));
  }

  private List<TaskSupplier> argumentTasks(Scope<TaskSupplier> scope, TaskSupplier function,
      List<Optional<Expression>> arguments) {
    var builder = ImmutableList.<TaskSupplier>builder();
    for (int i = 0; i < arguments.size(); i++) {
      Optional<Expression> argument = arguments.get(i);
      if (argument.isPresent()) {
        builder.add(argument.get().visit(scope, this));
      } else {
        builder.add(defaultValueTask(function, i, scope));
      }
    }
    return builder.build();
  }

  private TaskSupplier defaultValueTask(TaskSupplier function, int index,
      Scope<TaskSupplier> scope) {
    Type type = ((FunctionType) function.type()).parameterTypes().get(index);
    // TODO this location is not correct
    Location location = function.location();
    // TODO task name is not precise
    Supplier<Task> task = () -> new DefaultValueTask(
        type, "default parameter value", list(function), index, location, scope, this);
    return new TaskSupplier(type, location, task);
  }

  public TaskSupplier taskForDefaultValue(Scope<TaskSupplier> scope, String functionName,
      int index) {
    var function = (Function) definitions.referencables().get(functionName);
    var defaultValueExpression = function.parameters().get(index).defaultValue().get();
    return defaultValueExpression.visit(scope, this);
  }

  public Task taskForCall(Scope<TaskSupplier> scope, BoundedVariables variables,
      Type actualResultType, String functionName, ImmutableList<TaskSupplier> arguments,
      Location location) {
    var function = (Function) definitions.referencables().get(functionName);
    if (function instanceof RealFunction realFunction) {
      if (realFunction.body() instanceof NativeExpression nativ) {
        return taskForNativeFunction(scope, arguments, realFunction, nativ, variables,
            actualResultType, location);
      } else {
        return taskForDefinedFunction(scope, actualResultType, realFunction, arguments, location);
      }
    } else if (function instanceof Constructor constructor) {
      var resultType = constructor.type().resultType();
      var tupleSpec = (TupleSpec) resultType.visit(toSpecConverter);
      return taskForConstructorCall(CALL, resultType, tupleSpec, constructor.extendedName(),
          arguments, location);
    } else {
      throw new RuntimeException("Unexpected case: " + function.getClass().getCanonicalName());
    }
  }

  private Task taskForConstructorCall(TaskKind taskKind, Type resultType, TupleSpec tupleSpec,
      String name, List<TaskSupplier> dependencies, Location location) {
    var algorithm = new CreateTupleAlgorithm(tupleSpec);
    return new AlgorithmTask(taskKind, resultType, name, algorithm, dependencies, location);
  }

  private Task taskForDefinedFunction(Scope<TaskSupplier> scope, Type actualResultType,
      RealFunction function, List<TaskSupplier> arguments, Location location) {
    var newScope = new Scope<>(scope, nameToArgumentMap(function.parameters(), arguments));
    var taskSupplier = convertIfNeeded(actualResultType, function.body().visit(newScope, this));
    return new VirtualTask(CALL, function.extendedName(), taskSupplier, location);
  }

  private Task taskForNativeFunction(Scope<TaskSupplier> scope, List<TaskSupplier> arguments,
      RealFunction function, NativeExpression nativ, BoundedVariables variables, Type actualResultType,
      Location location) {
    var actualParameterTypes = map(
        function.type().parameterTypes(), t -> t.mapVariables(variables, LOWER));
    if (function.name().equals(IF_FUNCTION_NAME)) {
      var dependencies = convertedArguments(actualParameterTypes, arguments);
      return new IfTask(actualResultType, dependencies, location);
    } else {
      var nativeCode = visit(scope, nativ);
      var dependencies = concat(nativeCode, convertedArguments(actualParameterTypes, arguments));
      var algorithm = new CallNativeAlgorithm(methodLoader, actualResultType.visit(toSpecConverter),
          function, nativ.isPure());
      return new AlgorithmTask(CALL, actualResultType, function.extendedName(), algorithm,
          dependencies, location);
    }
  }

  private static ImmutableMap<String, TaskSupplier> nameToArgumentMap(
      List<Item> names, List<TaskSupplier> arguments) {
    var builder = ImmutableMap.<String, TaskSupplier>builder();
    for (int i = 0; i < arguments.size(); i++) {
      builder.put(names.get(i).name(), arguments.get(i));
    }
    return builder.build();
  }

  @Override
  public TaskSupplier visit(Scope<TaskSupplier> scope, ArrayLiteralExpression expression) {
    var elements = childrenTasks(scope, expression.elements());
    var actualType = arrayType(elements).orElse(expression.type());

    return new TaskSupplier(actualType, expression.location(), () -> {
      var algorithm = new CreateArrayAlgorithm(toSpecConverter.visit(actualType));
      var convertedElements = convertedElements(actualType.elemType(), elements);
      return new AlgorithmTask(LITERAL, actualType, actualType.name(), algorithm, convertedElements,
          expression.location());
    });
  }

  private static Optional<ArrayType> arrayType(List<TaskSupplier> elements) {
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
      return new AlgorithmTask(
          LITERAL, blob(), algorithm.shortedLiteral(), algorithm, list(), expression.location());
    });
  }

  @Override
  public TaskSupplier visit(Scope<TaskSupplier> scope, StringLiteralExpression expression) {
    return fixedStringTask(expression.string(), expression.location());
  }

  @Override
  public TaskSupplier visit(Scope<TaskSupplier> context, NativeExpression expression) {
    return fixedStringTask(expression.path(), expression.location());
  }

  private TaskSupplier fixedStringTask(String string, Location location) {
    return new TaskSupplier(string(), location, () -> {
      var stringType = toSpecConverter.visit(string());
      var algorithm = new FixedStringAlgorithm(stringType, string);
      var name = algorithm.shortedString();
      return new AlgorithmTask(TaskKind.LITERAL, string(), name, algorithm, list(), location);
    });
  }

  private List<TaskSupplier> childrenTasks(Scope<TaskSupplier> scope, List<Expression> children) {
    return map(children, ch -> ch.visit(scope, this));
  }

  private ImmutableList<TaskSupplier> convertedElements(Type type, List<TaskSupplier> elements) {
    return map(elements, e -> convertIfNeeded(type, e));
  }

  private List<TaskSupplier> convertedArguments(List<Type> types, List<TaskSupplier> arguments) {
    return zip(types, arguments, this::convertIfNeeded);
  }

  private TaskSupplier convertIfNeeded(Type requiredType, TaskSupplier task) {
    if (task.type().equals(requiredType)) {
      return task;
    } else {
      return convert(requiredType, task);
    }
  }

  private TaskSupplier convert(Type requiredType, TaskSupplier task) {
    return new TaskSupplier(requiredType, task.location(), () -> {
      var description = requiredType.name() + "<-" + task.type().name();
      var algorithm = new ConvertAlgorithm(requiredType.visit(toSpecConverter));
      var dependencies = list(task);
      return new AlgorithmTask(
          CONVERSION, requiredType, description, algorithm, dependencies, task.location());
    });
  }
}
