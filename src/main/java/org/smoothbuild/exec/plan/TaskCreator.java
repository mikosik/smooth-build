package org.smoothbuild.exec.plan;

import static org.smoothbuild.exec.compute.TaskKind.CALL;
import static org.smoothbuild.exec.compute.TaskKind.CONVERSION;
import static org.smoothbuild.exec.compute.TaskKind.FIELD_READ;
import static org.smoothbuild.exec.compute.TaskKind.LITERAL;
import static org.smoothbuild.exec.compute.TaskKind.REFERENCE;
import static org.smoothbuild.exec.compute.TaskKind.VALUE;
import static org.smoothbuild.lang.base.define.Location.commandLineLocation;
import static org.smoothbuild.lang.base.type.BoundsMap.boundsMap;
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
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.db.object.spec.TupleSpec;
import org.smoothbuild.exec.algorithm.CallNativeAlgorithm;
import org.smoothbuild.exec.algorithm.ConvertAlgorithm;
import org.smoothbuild.exec.algorithm.CreateArrayAlgorithm;
import org.smoothbuild.exec.algorithm.CreateTupleAlgorithm;
import org.smoothbuild.exec.algorithm.FixedBlobAlgorithm;
import org.smoothbuild.exec.algorithm.FixedStringAlgorithm;
import org.smoothbuild.exec.algorithm.ReadTupleElementAlgorithm;
import org.smoothbuild.exec.algorithm.ReferenceAlgorithm;
import org.smoothbuild.exec.compute.AlgorithmTask;
import org.smoothbuild.exec.compute.DefaultValueTask;
import org.smoothbuild.exec.compute.EvaluateTask;
import org.smoothbuild.exec.compute.IfTask;
import org.smoothbuild.exec.compute.LazyTask;
import org.smoothbuild.exec.compute.MapTask;
import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.exec.compute.TaskKind;
import org.smoothbuild.exec.compute.VirtualTask;
import org.smoothbuild.exec.java.MethodLoader;
import org.smoothbuild.lang.base.define.Constructor;
import org.smoothbuild.lang.base.define.DefinedFunction;
import org.smoothbuild.lang.base.define.DefinedValue;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.Function;
import org.smoothbuild.lang.base.define.GlobalReferencable;
import org.smoothbuild.lang.base.define.IfFunction;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.MapFunction;
import org.smoothbuild.lang.base.define.NativeFunction;
import org.smoothbuild.lang.base.define.NativeValue;
import org.smoothbuild.lang.base.define.Value;
import org.smoothbuild.lang.base.type.ArrayType;
import org.smoothbuild.lang.base.type.BoundsMap;
import org.smoothbuild.lang.base.type.FunctionType;
import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.Types;
import org.smoothbuild.lang.expr.ArrayLiteralExpression;
import org.smoothbuild.lang.expr.BlobLiteralExpression;
import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.FieldReadExpression;
import org.smoothbuild.lang.expr.NativeExpression;
import org.smoothbuild.lang.expr.ParameterReferenceExpression;
import org.smoothbuild.lang.expr.ReferenceExpression;
import org.smoothbuild.lang.expr.StringLiteralExpression;
import org.smoothbuild.util.Scope;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class TaskCreator {
  private final Definitions definitions;
  private final TypeToSpecConverter toSpecConverter;
  private final MethodLoader methodLoader;

  @Inject
  public TaskCreator(Definitions definitions, TypeToSpecConverter toSpecConverter,
      MethodLoader methodLoader) {
    this.definitions = definitions;
    this.toSpecConverter = toSpecConverter;
    this.methodLoader = methodLoader;
  }

  public Task taskFor(Expression expression, Scope<Task> scope) {
    return lazyTaskFor(expression, scope);
  }

  private Task lazyTaskFor(Expression expression, Scope<Task> scope) {
    // TODO refactor to pattern matching once we have java 17
    if (expression instanceof NativeExpression nativ) {
      return nativeLazyTask(nativ);
    } else if (expression instanceof CallExpression call) {
      return callLazyTask(scope, call);
    } else if (expression instanceof FieldReadExpression fieldRead) {
      return fieldReadLazyTask(scope, fieldRead);
    } else if (expression instanceof ParameterReferenceExpression parameterReference) {
      return parameterReferenceLazyTask(scope, parameterReference);
    } else if (expression instanceof ReferenceExpression reference) {
      return referenceLazyTask(scope, reference);
    } else if (expression instanceof ArrayLiteralExpression arrayLiteral) {
      return arrayLiteralLazyTask(scope, arrayLiteral);
    } else if (expression instanceof BlobLiteralExpression blobLiteral) {
      return blobLiteralLazyTask(blobLiteral);
    } else if (expression instanceof StringLiteralExpression stringLiteral) {
      return stringLiteralLazyTask(stringLiteral);
    } else {
      throw new IllegalArgumentException(
          "Unknown expression " + expression.getClass().getCanonicalName() + ".");
    }
  }

  private LazyTask nativeLazyTask(NativeExpression nativ) {
    return stringLiteralLazyTask(nativ.path());
  }

  private LazyTask callLazyTask(Scope<Task> scope, CallExpression call) {
    var function = taskFor(call.function(), scope);
    var arguments = argumentLazyTasks(scope, function, call.arguments(), call.location());
    return callLazyTask(scope, function, arguments, call.location());
  }

  public LazyTask callLazyTask(Scope<Task> scope, Task function, List<Task> arguments,
      Location location) {
    var variables = inferVariablesInFunctionCall(function, arguments);
    var functionType = (FunctionType) function.type();
    var actualResultType = functionType.resultType().mapVariables(variables, LOWER);

    return new LazyTask(actualResultType, location,
        () -> callTask(scope, function, arguments, location, variables));
  }

  public EvaluateTask callTask(Scope<Task> scope, Task function, List<Task> arguments,
      Location location) {
    var variables = inferVariablesInFunctionCall(function, arguments);
    return callTask(scope, function, arguments, location, variables);
  }

  private EvaluateTask callTask(Scope<Task> scope, Task function, List<Task> arguments,
      Location location, BoundsMap variables) {
    var functionType = (FunctionType) function.type();
    var actualResultType = functionType.resultType().mapVariables(variables, LOWER);
    return new EvaluateTask(
        actualResultType, function, arguments, location, variables, scope, this);
  }

  private static BoundsMap inferVariablesInFunctionCall(Task function, List<Task> arguments) {
    var functionType = (FunctionType) function.type();
    var argumentTypes = map(arguments, Task::type);
    return inferVariableBounds(functionType.parameterTypes(), argumentTypes, LOWER);
  }

  private List<Task> argumentLazyTasks(Scope<Task> scope, Task function,
      List<Optional<Expression>> arguments, Location location) {
    var builder = ImmutableList.<Task>builder();
    for (int i = 0; i < arguments.size(); i++) {
      builder.add(arguments.get(i)
          .map(a -> lazyTaskFor(a, scope))
          .orElse(defaultValueLazyTask(function, i, scope, location)));
    }
    return builder.build();
  }

  private LazyTask defaultValueLazyTask(Task function, int index, Scope<Task> scope,
      Location location) {
    var type = ((FunctionType) function.type()).parameterTypes().get(index);
    // TODO task name is not precise
    return new LazyTask(type, location,
        () -> new DefaultValueTask(
            type, "default parameter value", list(function), index, location, scope, this));
  }

  private LazyTask fieldReadLazyTask(Scope<Task> scope, FieldReadExpression fieldRead) {
    var type = fieldRead.field().type();
    var location = fieldRead.location();
    return new LazyTask(type, location, () -> fieldReadTask(scope, fieldRead, type));
  }

  private Task fieldReadTask(Scope<Task> scope, FieldReadExpression expression, Type type) {
    var structType = (StructType) expression.expression().type();
    var name = expression.field().name().get();
    var algorithm = new ReadTupleElementAlgorithm(
        structType.fieldIndex(name), toSpecConverter.visit(type));
    var dependencies = list(taskFor(expression.expression(), scope));
    return new AlgorithmTask(
        FIELD_READ, type, "." + name, algorithm, dependencies, expression.location());
  }

  private Task parameterReferenceLazyTask(Scope<Task> scope,
      ParameterReferenceExpression parameterReference) {
    return scope.get(parameterReference.name());
  }

  private LazyTask referenceLazyTask(Scope<Task> scope, ReferenceExpression reference) {
    var referencable = definitions.referencables().get(reference.name());
    var type = referencable.type().strip();
    return new LazyTask(type, reference.location(),
        () -> referenceTask(scope, reference, referencable, type));
  }

  private Task referenceTask(Scope<Task> scope, ReferenceExpression reference,
      GlobalReferencable referencable, Type type) {
    var module = definitions.modules().get(referencable.modulePath());
    var algorithm = new ReferenceAlgorithm(referencable, module, toSpecConverter.functionSpec());
    AlgorithmTask task = new AlgorithmTask(
        REFERENCE, type, ":" + referencable.name(), algorithm, list(), reference.location());
    if (referencable instanceof Value) {
      return new EvaluateTask(
          type, task, list(), reference.location(), boundsMap(), scope, this);
    } else {
      return task;
    }
  }

  private LazyTask arrayLiteralLazyTask(Scope<Task> scope, ArrayLiteralExpression arrayLiteral) {
    var elements = map(arrayLiteral.elements(), e -> taskFor(e, scope));
    var actualType = arrayType(elements).orElse(arrayLiteral.type());

    return new LazyTask(actualType, arrayLiteral.location(),
        () -> arrayLiteralTask(arrayLiteral, elements, actualType));
  }

  private static Optional<ArrayType> arrayType(List<Task> elements) {
    return elements
        .stream()
        .map(Task::type)
        .reduce((typeA, typeB) -> typeA.mergeWith(typeB, UPPER))
        .map(Types::array);
  }

  private Task arrayLiteralTask(ArrayLiteralExpression expression, List<Task> elements,
      ArrayType actualType) {
    var convertedElements = map(elements, e -> convertIfNeeded(actualType.elemType(), e));
    return arrayLiteralTask(LITERAL, actualType, convertedElements, expression.location());
  }

  public AlgorithmTask arrayLiteralTask(TaskKind taskKind, ArrayType type,
      ImmutableList<Task> elements, Location location) {
    var algorithm = new CreateArrayAlgorithm(toSpecConverter.visit(type));
    return new AlgorithmTask(taskKind, type, "[]", algorithm, elements, location);
  }

  private LazyTask blobLiteralLazyTask(BlobLiteralExpression blobLiteral) {
    return new LazyTask(blob(), blobLiteral.location(), () -> blobLiteralTask(blobLiteral));
  }

  private Task blobLiteralTask(BlobLiteralExpression expression) {
    var blobSpec = toSpecConverter.visit(blob());
    var algorithm = new FixedBlobAlgorithm(blobSpec, expression.byteString());
    return new AlgorithmTask(
        LITERAL, blob(), algorithm.shortedLiteral(), algorithm, list(), expression.location());
  }

  private LazyTask stringLiteralLazyTask(StringLiteralExpression stringLiteral) {
    Location location = stringLiteral.location();
    return new LazyTask(string(), location,
        () -> stringLiteralTask(stringLiteral.string(), location));
  }

  private Task stringLiteralTask(String string, Location location) {
    var stringType = toSpecConverter.visit(string());
    var algorithm = new FixedStringAlgorithm(stringType, string);
    var name = algorithm.shortedString();
    return new AlgorithmTask(LITERAL, string(), name, algorithm, list(), location);
  }

  public Task taskForNamedFunctionParameterDefaultValue(Scope<Task> scope, String functionName,
      int index) {
    var function = (Function) definitions.referencables().get(functionName);
    var defaultValueExpression = function.parameters().get(index).defaultValue().get();
    return taskFor(defaultValueExpression, scope);
  }

  public Task taskForEvaluatingLambda(Scope<Task> scope, BoundsMap variables,
      Type actualResultType, String name, List<Task> arguments, Location location) {
    var referencable = definitions.referencables().get(name);
    if (referencable instanceof Value value) {
      return valueTask(scope, value, location);
    } else if (referencable instanceof DefinedFunction definedFunction) {
      return definedFunctionTask(scope, actualResultType, definedFunction, arguments, location);
    } else if (referencable instanceof NativeFunction nativeFunction) {
      return callNativeFunctionTask(scope, arguments, nativeFunction, nativeFunction.nativ(),
          variables, actualResultType, location);
    } else if (referencable instanceof IfFunction) {
      return new IfTask(actualResultType, arguments, location);
    } else if (referencable instanceof MapFunction) {
      return new MapTask(actualResultType, arguments, location, scope, this);
    } else if (referencable instanceof Constructor constructor) {
      var resultType = constructor.type().resultType();
      var tupleSpec = (TupleSpec) toSpecConverter.visit(resultType);
      return constructorCallTask(resultType, tupleSpec, constructor.extendedName(),
          arguments, location);
    } else {
      throw new IllegalArgumentException(
          "Unexpected case: " + referencable.getClass().getCanonicalName());
    }
  }

  public Task commandLineValueTask(Value value) {
    return valueTask(new Scope<>(Map.of()), value, commandLineLocation());
  }

  private Task valueTask(Scope<Task> scope, Value value, Location location) {
    if (value instanceof DefinedValue definedValue) {
      return definedValueTask(scope, definedValue, location);
    } else if (value instanceof NativeValue nativeValue) {
      return callNativeValueTask(nativeValue, location);
    } else {
      throw new IllegalArgumentException(
          "Unexpected case: " + value.getClass().getCanonicalName());
    }
  }

  private Task definedValueTask(Scope<Task> scope, DefinedValue definedValue, Location location) {
    var task = taskFor(definedValue.body(), scope);
    var convertedTask = convertIfNeeded(definedValue.type(), task);
    return new VirtualTask(VALUE, definedValue.extendedName(), convertedTask, location);
  }

  private Task callNativeValueTask(NativeValue nativeValue, Location location) {
    NativeExpression nativ = nativeValue.nativ();
    var algorithm = new CallNativeAlgorithm(
        methodLoader, toSpecConverter.visit(nativeValue.type()), nativeValue, nativ.isPure());
    var nativeCode = nativeLazyTask(nativ);
    return new AlgorithmTask(VALUE, nativeValue.type(), nativeValue.extendedName(), algorithm,
        list(nativeCode), location);
  }

  private Task definedFunctionTask(Scope<Task> scope, Type actualResultType,
      DefinedFunction function, List<Task> arguments, Location location) {
    var newScope = new Scope<>(scope, nameToArgumentMap(function.parameters(), arguments));
    var body = taskFor(function.body(), newScope);
    var convertedTask = convertIfNeeded(actualResultType, body);
    return new VirtualTask(CALL, function.extendedName(), convertedTask, location);
  }

  private static Map<String, Task> nameToArgumentMap(List<Item> names, List<Task> arguments) {
    var mapEntries = zip(names, arguments, (n, a) -> Map.entry(n.name(), a));
    return ImmutableMap.copyOf(mapEntries);
  }

  private Task callNativeFunctionTask(Scope<Task> scope, List<Task> arguments,
      NativeFunction function, NativeExpression nativ, BoundsMap variables,
      Type actualResultType, Location location) {
    var algorithm = new CallNativeAlgorithm(methodLoader, toSpecConverter.visit(actualResultType),
        function, nativ.isPure());
    var dependencies = concat(
        taskFor(nativ, scope), convertedArgumentTasks(arguments, function, variables));
    return new AlgorithmTask(CALL, actualResultType, function.extendedName(), algorithm,
        dependencies, location);
  }

  private ImmutableList<Task> convertedArgumentTasks(List<Task> arguments, NativeFunction function,
      BoundsMap variables) {
    var actualTypes = map(function.type().parameterTypes(), t -> t.mapVariables(variables, LOWER));
    return zip(actualTypes, arguments, this::convertIfNeeded);
  }

  private Task constructorCallTask(Type resultType, TupleSpec tupleSpec, String name,
      List<Task> arguments, Location location) {
    var algorithm = new CreateTupleAlgorithm(tupleSpec);
    return new AlgorithmTask(CALL, resultType, name, algorithm, arguments, location);
  }

  private Task convertIfNeeded(Type requiredType, Task task) {
    if (task.type().equals(requiredType)) {
      return task;
    } else {
      return convertTask(requiredType, task);
    }
  }

  private Task convertTask(Type requiredType, Task task) {
    var description = requiredType.name() + "<-" + task.type().name();
    var algorithm = new ConvertAlgorithm(toSpecConverter.visit(requiredType));
    return new AlgorithmTask(
        CONVERSION, requiredType, description, algorithm, list(task), task.location());
  }
}
