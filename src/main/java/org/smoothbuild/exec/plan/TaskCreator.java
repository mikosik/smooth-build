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
import static org.smoothbuild.lang.base.type.Types.blobT;
import static org.smoothbuild.lang.base.type.Types.intT;
import static org.smoothbuild.lang.base.type.Types.stringT;
import static org.smoothbuild.util.Lists.concat;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Lists.zip;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import javax.inject.Inject;

import org.smoothbuild.db.object.spec.val.RecSpec;
import org.smoothbuild.exec.algorithm.CallNativeAlgorithm;
import org.smoothbuild.exec.algorithm.ConvertAlgorithm;
import org.smoothbuild.exec.algorithm.CreateArrayAlgorithm;
import org.smoothbuild.exec.algorithm.CreateRecAlgorithm;
import org.smoothbuild.exec.algorithm.FixedBlobAlgorithm;
import org.smoothbuild.exec.algorithm.FixedIntAlgorithm;
import org.smoothbuild.exec.algorithm.FixedStringAlgorithm;
import org.smoothbuild.exec.algorithm.ReadRecItemAlgorithm;
import org.smoothbuild.exec.algorithm.ReferenceAlgorithm;
import org.smoothbuild.exec.compute.AlgorithmTask;
import org.smoothbuild.exec.compute.DefaultArgumentTask;
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
import org.smoothbuild.lang.expr.IntLiteralExpression;
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
  private final Map<Class<?>, Handler<?>> map;

  @Inject
  public TaskCreator(Definitions definitions, TypeToSpecConverter toSpecConverter,
      MethodLoader methodLoader) {
    this(definitions, toSpecConverter, methodLoader, ImmutableMap.of());
  }

  // Visible for testing
  TaskCreator(Definitions definitions, TypeToSpecConverter toSpecConverter,
      MethodLoader methodLoader, Map<Class<?>, Handler<?>> additionalHandlers) {
    this.definitions = definitions;
    this.toSpecConverter = toSpecConverter;
    this.methodLoader = methodLoader;
    this.map = constructHandlers(additionalHandlers);
  }

  private ImmutableMap<Class<?>, Handler<?>> constructHandlers(
      Map<Class<?>, Handler<?>> additionalHandlers) {
    return ImmutableMap.<Class<?>, Handler<?>>builder()
        .put(NativeExpression.class,
            new Handler<>(this::nativeLazy, this::nativeEager))
        .put(CallExpression.class,
            new Handler<>(this::callLazy, this::callEager))
        .put(FieldReadExpression.class,
            new Handler<>(this::fieldReadLazy, this::fieldReadEager))
        .put(ParameterReferenceExpression.class,
            new Handler<>(this::paramReferenceLazyTask, this::paramReferenceLazyTask))
        .put(ReferenceExpression.class,
            new Handler<>(this::referenceLazy, this::referenceEager))
        .put(ArrayLiteralExpression.class,
            new Handler<>(this::arrayLazy, this::arrayEager))
        .put(BlobLiteralExpression.class,
            new Handler<>(this::blobLazy, this::blobEager))
        .put(IntLiteralExpression.class,
            new Handler<>(this::intLazy, this::intEager))
        .put(StringLiteralExpression.class,
            new Handler<>(this::stringLazy, this::stringEager))
        .putAll(additionalHandlers)
        .build();
  }

  public Task taskFor(Scope<Task> scope, Expression expression, boolean eager) {
    return handlerFor(expression).task(eager).apply(scope, expression);
  }

  public Task eagerTaskFor(Scope<Task> scope, Expression expression) {
    return handlerFor(expression).eagerTask().apply(scope, expression);
  }

  private Task lazyTaskFor(Scope<Task> scope, Expression expression) {
    return handlerFor(expression).lazyTask().apply(scope, expression);
  }

  public <T> Handler<T> handlerFor(Expression expression) {
    @SuppressWarnings("unchecked")
    Handler<T> result = (Handler<T>) map.get(expression.getClass());
    if (result == null) {
      System.out.println("expression.getClass() = " + expression.getClass());
    }
    return result;
  }

  // NativeExpression

  private Task nativeLazy(Scope<Task> scope, NativeExpression nativ) {
    return nativeLazyTask(nativ);
  }

  private Task nativeEager(Scope<Task> scope, NativeExpression nativ) {
    return stringEagerTask(nativ.path());
  }

  private Task nativeLazyTask(NativeExpression nativ) {
    return stringLazyTask(nativ.path());
  }

  private Task nativeEagerTask(NativeExpression nativ) {
    return stringEagerTask(nativ.path());
  }

  // CallExpression

  private Task callLazy(Scope<Task> scope, CallExpression call) {
    return callTask(scope, call, false);
  }

  private Task callEager(Scope<Task> scope, CallExpression call) {
    return callTask(scope, call, true);
  }

  private Task callTask(Scope<Task> scope, CallExpression call, boolean eager) {
    var function = taskFor(scope, call.function(), eager);
    var arguments = argumentLazyTasks(scope, function, call.arguments(), call.location());
    Location location = call.location();
    var variables = inferVariablesInFunctionCall(function, arguments);
    return callTask(scope, function, arguments, location, variables, eager);
  }

  private Task callTask(Scope<Task> scope, Task function, List<Task> arguments,
      Location location, BoundsMap variables, boolean eager) {
    if (eager) {
      return callEagerTask(scope, function, arguments, location, variables);
    } else {
      var functionType = (FunctionType) function.type();
      var actualResultType = functionType.resultType().mapVariables(variables, LOWER);
      return new LazyTask(actualResultType, location,
          () -> callEagerTask(scope, function, arguments, location, variables));
    }
  }

  public EvaluateTask callEagerTask(Scope<Task> scope, Task function, List<Task> arguments,
      Location location) {
    var variables = inferVariablesInFunctionCall(function, arguments);
    return callEagerTask(scope, function, arguments, location, variables);
  }

  private EvaluateTask callEagerTask(Scope<Task> scope, Task function, List<Task> arguments,
      Location location, BoundsMap variables) {
    var functionType = (FunctionType) function.type();
    var actualResultType = functionType.resultType().mapVariables(variables, LOWER);
    return new EvaluateTask(
        actualResultType, function, arguments, location, variables, scope, TaskCreator.this);
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
          .map(a -> lazyTaskFor(scope, a))
          .orElse(defaultArgumentLazyTask(function, i, scope, location)));
    }
    return builder.build();
  }

  private LazyTask defaultArgumentLazyTask(Task function, int index, Scope<Task> scope,
      Location location) {
    var type = ((FunctionType) function.type()).parameterTypes().get(index);
    return new LazyTask(type, location, () -> new DefaultArgumentTask(type,
        "default parameter value", list(function), index, location, scope, TaskCreator.this));
  }

  // FieldReadExpression

  private Task fieldReadLazy(Scope<Task> scope, FieldReadExpression fieldRead) {
    var type = fieldRead.field().type();
    var location = fieldRead.location();
    return new LazyTask(type, location, () -> fieldReadEagerTask(scope, fieldRead, type));
  }

  private Task fieldReadEager(Scope<Task> scope, FieldReadExpression fieldRead) {
    var type = fieldRead.field().type();
    return fieldReadEagerTask(scope, fieldRead, type);
  }

  private Task fieldReadEagerTask(Scope<Task> scope, FieldReadExpression expression, Type type) {
    var structType = (StructType) expression.expression().type();
    var name = expression.field().name().get();
    var algorithm = new ReadRecItemAlgorithm(
        structType.fieldIndex(name), toSpecConverter.visit(type));
    var dependencies = list(eagerTaskFor(scope, expression.expression()));
    return new AlgorithmTask(
        FIELD_READ, type, "." + name, algorithm, dependencies, expression.location());
  }

  // ParameterReferenceExpression

  private Task paramReferenceLazyTask(Scope<Task> scope,
      ParameterReferenceExpression parameterReference) {
    return scope.get(parameterReference.name());
  }

  // ReferenceExpression

  private Task referenceLazy(Scope<Task> scope, ReferenceExpression reference) {
    var type = reference.type().strip();
    return new LazyTask(type, reference.location(),
        () -> referenceEagerTask(scope, reference, type));
  }

  private Task referenceEager(Scope<Task> scope, ReferenceExpression reference) {
    return referenceEagerTask(scope, reference, reference.type().strip());
  }

  private Task referenceEagerTask(Scope<Task> scope, ReferenceExpression reference, Type type) {
    var referencable = definitions.referencables().get(reference.name());
    var module = definitions.modules().get(referencable.modulePath());
    var algorithm = new ReferenceAlgorithm(referencable, module, toSpecConverter.functionSpec());
    var task = new AlgorithmTask(
        REFERENCE, type, ":" + referencable.name(), algorithm, list(), reference.location());
    if (referencable instanceof Value) {
      return new EvaluateTask(
          type, task, list(), reference.location(), boundsMap(), scope, TaskCreator.this);
    } else {
      return task;
    }
  }

  // ArrayLiteralExpression

  private Task arrayLazy(Scope<Task> scope, ArrayLiteralExpression arrayLiteral) {
    var elements = map(arrayLiteral.elements(), e -> lazyTaskFor(scope, e));
    var actualType = arrayType(elements).orElse(arrayLiteral.type());

    return new LazyTask(actualType, arrayLiteral.location(),
        () -> arrayEagerTask(arrayLiteral, elements, actualType));
  }

  private Task arrayEager(Scope<Task> scope, ArrayLiteralExpression arrayLiteral) {
    var elements = map(arrayLiteral.elements(), e -> eagerTaskFor(scope, e));
    var actualType = arrayType(elements).orElse(arrayLiteral.type());
    return arrayEagerTask(arrayLiteral, elements, actualType);
  }

  private static Optional<ArrayType> arrayType(List<Task> elements) {
    return elements
        .stream()
        .map(Task::type)
        .reduce((typeA, typeB) -> typeA.mergeWith(typeB, UPPER))
        .map(Types::arrayT);
  }

  private Task arrayEagerTask(ArrayLiteralExpression expression, List<Task> elements,
      ArrayType actualType) {
    var convertedElements = map(elements, e -> convertIfNeededEagerTask(actualType.elemType(), e));
    return arrayEagerTask(LITERAL, actualType, convertedElements, expression.location());
  }

  public AlgorithmTask arrayEagerTask(TaskKind taskKind, ArrayType type,
      ImmutableList<Task> elements, Location location) {
    var algorithm = new CreateArrayAlgorithm(toSpecConverter.visit(type));
    return new AlgorithmTask(taskKind, type, "[]", algorithm, elements, location);
  }

  // BlobLiteralExpression

  private Task blobLazy(Scope<Task> scope, BlobLiteralExpression blobLiteral) {
    return new LazyTask(blobT(), blobLiteral.location(), () -> blogEagerTask(blobLiteral));
  }

  private Task blobEager(Scope<Task> scope, BlobLiteralExpression expression) {
    return blogEagerTask(expression);
  }

  private AlgorithmTask blogEagerTask(BlobLiteralExpression expression) {
    var blobSpec = toSpecConverter.visit(blobT());
    var algorithm = new FixedBlobAlgorithm(blobSpec, expression.byteString());
    return new AlgorithmTask(
        LITERAL, blobT(), algorithm.shortedLiteral(), algorithm, list(), expression.location());
  }

  // IntLiteralExpression

  private Task intLazy(Scope<Task> scope, IntLiteralExpression intLiteral) {
    return new LazyTask(intT(), intLiteral.location(), () -> intEagerTask(intLiteral));
  }

  private Task intEager(Scope<Task> scope, IntLiteralExpression intLiteral) {
    return intEagerTask(intLiteral);
  }

  private AlgorithmTask intEagerTask(IntLiteralExpression expression) {
    var intSpec = toSpecConverter.visit(intT());
    var bigInteger = expression.bigInteger();
    var algorithm = new FixedIntAlgorithm(intSpec, bigInteger);
    return new AlgorithmTask(
        LITERAL, intT(), bigInteger.toString(), algorithm, list(), expression.location());
  }

  // StringLiteralExpression

  private Task stringLazy(Scope<Task> scope, StringLiteralExpression stringLiteral) {
    return stringLazyTask(stringLiteral);
  }

  private Task stringEager(Scope<Task> scope, StringLiteralExpression stringLiteral) {
    return stringEagerTask(stringLiteral);
  }

  private LazyTask stringLazyTask(StringLiteralExpression stringLiteral) {
    return new LazyTask(stringT(), stringLiteral.location(),
        () -> stringEagerTask(stringLiteral));
  }

  private Task stringEagerTask(StringLiteralExpression stringLiteral) {
    var stringType = toSpecConverter.visit(stringT());
    var algorithm = new FixedStringAlgorithm(stringType, stringLiteral.string());
    var name = algorithm.shortedString();
    return new AlgorithmTask(
        LITERAL, stringT(), name, algorithm, list(), stringLiteral.location());
  }

  // helper methods

  public Task defaultArgumentEagerTask(Scope<Task> scope, String functionName, int index) {
    var function = (Function) definitions.referencables().get(functionName);
    var defaultArgumentExpression = function.parameters().get(index).defaultValue().get();
    return eagerTaskFor(scope, defaultArgumentExpression);
  }

  public Task evaluateLambdaEagerTask(Scope<Task> scope, BoundsMap variables,
      Type actualResultType, String name, List<Task> arguments, Location location) {
    var referencable = definitions.referencables().get(name);
    if (referencable instanceof Value value) {
      return valueEagerTask(scope, value, location);
    } else if (referencable instanceof DefinedFunction definedFunction) {
      return definedFunctionEagerTask(scope, actualResultType, definedFunction, arguments, location);
    } else if (referencable instanceof NativeFunction nativeFunction) {
      return callNativeFunctionEagerTask(scope, arguments, nativeFunction, nativeFunction.nativ(),
          variables, actualResultType, location);
    } else if (referencable instanceof IfFunction) {
      return new IfTask(actualResultType, arguments, location);
    } else if (referencable instanceof MapFunction) {
      return new MapTask(actualResultType, arguments, location, scope, this);
    } else if (referencable instanceof Constructor constructor) {
      var resultType = constructor.type().resultType();
      var recSpec = (RecSpec) toSpecConverter.visit(resultType);
      return constructorCallEagerTask(resultType, recSpec, constructor.extendedName(),
          arguments, location);
    } else {
      throw new IllegalArgumentException(
          "Unexpected case: " + referencable.getClass().getCanonicalName());
    }
  }

  public Task commandLineValueEagerTask(Value value) {
    return valueEagerTask(new Scope<>(Map.of()), value, commandLineLocation());
  }

  private Task valueEagerTask(Scope<Task> scope, Value value, Location location) {
    if (value instanceof DefinedValue definedValue) {
      return definedValueEagerTask(scope, definedValue, location);
    } else if (value instanceof NativeValue nativeValue) {
      return callNativeValueEagerTask(nativeValue, location);
    } else {
      throw new IllegalArgumentException(
          "Unexpected case: " + value.getClass().getCanonicalName());
    }
  }

  private Task definedValueEagerTask(Scope<Task> scope, DefinedValue definedValue,
      Location location) {
    var task = eagerTaskFor(scope, definedValue.body());
    var convertedTask = convertIfNeededEagerTask(definedValue.type(), task);
    return new VirtualTask(VALUE, definedValue.extendedName(), convertedTask, location);
  }

  private Task callNativeValueEagerTask(NativeValue nativeValue, Location location) {
    NativeExpression nativ = nativeValue.nativ();
    var algorithm = new CallNativeAlgorithm(
        methodLoader, toSpecConverter.visit(nativeValue.type()), nativeValue, nativ.isPure());
    var nativeCode = nativeEagerTask(nativ);
    return new AlgorithmTask(VALUE, nativeValue.type(), nativeValue.extendedName(), algorithm,
        list(nativeCode), location);
  }

  private Task definedFunctionEagerTask(Scope<Task> scope, Type actualResultType,
      DefinedFunction function, List<Task> arguments, Location location) {
    var newScope = new Scope<>(scope, nameToArgumentMap(function.parameters(), arguments));
    var body = eagerTaskFor(newScope, function.body());
    var convertedTask = convertIfNeededEagerTask(actualResultType, body);
    return new VirtualTask(CALL, function.extendedName(), convertedTask, location);
  }

  private static Map<String, Task> nameToArgumentMap(List<Item> names, List<Task> arguments) {
    var mapEntries = zip(names, arguments, (n, a) -> Map.entry(n.name(), a));
    return ImmutableMap.copyOf(mapEntries);
  }

  private Task callNativeFunctionEagerTask(Scope<Task> scope, List<Task> arguments,
      NativeFunction function, NativeExpression nativ, BoundsMap variables,
      Type actualResultType, Location location) {
    var algorithm = new CallNativeAlgorithm(
        methodLoader, toSpecConverter.visit(actualResultType), function, nativ.isPure());
    var dependencies = concat(
        eagerTaskFor(scope, nativ),
        convertedArgumentEagerTasks(arguments, function, variables));
    return new AlgorithmTask(CALL, actualResultType, function.extendedName(), algorithm,
        dependencies, location);
  }

  private ImmutableList<Task> convertedArgumentEagerTasks(
      List<Task> arguments, NativeFunction function, BoundsMap variables) {
    var actualTypes = map(function.type().parameterTypes(), t -> t.mapVariables(variables, LOWER));
    return zip(actualTypes, arguments, this::convertIfNeededEagerTask);
  }

  private Task constructorCallEagerTask(Type resultType, RecSpec recSpec, String name,
      List<Task> arguments, Location location) {
    var algorithm = new CreateRecAlgorithm(recSpec);
    return new AlgorithmTask(CALL, resultType, name, algorithm, arguments, location);
  }

  private Task convertIfNeededEagerTask(Type requiredType, Task task) {
    if (task.type().equals(requiredType)) {
      return task;
    } else {
      return convertEagerTask(requiredType, task);
    }
  }

  private Task convertEagerTask(Type requiredType, Task task) {
    var description = requiredType.name() + "<-" + task.type().name();
    var algorithm = new ConvertAlgorithm(toSpecConverter.visit(requiredType));
    return new AlgorithmTask(
        CONVERSION, requiredType, description, algorithm, list(task), task.location());
  }

  public record Handler<E>(
      BiFunction<Scope<Task>, E, Task> lazyTask,
      BiFunction<Scope<Task>, E, Task> eagerTask) {
    public BiFunction<Scope<Task>, E, Task> task(boolean eager) {
      return eager ? eagerTask : lazyTask;
    }
  }
}
