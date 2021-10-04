package org.smoothbuild.exec.plan;

import static org.smoothbuild.exec.job.TaskKind.CALL;
import static org.smoothbuild.exec.job.TaskKind.CONVERSION;
import static org.smoothbuild.exec.job.TaskKind.LITERAL;
import static org.smoothbuild.exec.job.TaskKind.REFERENCE;
import static org.smoothbuild.exec.job.TaskKind.SELECT;
import static org.smoothbuild.exec.job.TaskKind.VALUE;
import static org.smoothbuild.lang.base.define.Location.commandLineLocation;
import static org.smoothbuild.lang.base.type.BoundsMap.boundsMap;
import static org.smoothbuild.lang.base.type.Types.blobT;
import static org.smoothbuild.lang.base.type.Types.intT;
import static org.smoothbuild.lang.base.type.Types.lower;
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
import org.smoothbuild.exec.java.MethodLoader;
import org.smoothbuild.exec.job.ApplyJob;
import org.smoothbuild.exec.job.DefaultArgumentJob;
import org.smoothbuild.exec.job.IfJob;
import org.smoothbuild.exec.job.Job;
import org.smoothbuild.exec.job.LazyJob;
import org.smoothbuild.exec.job.MapJob;
import org.smoothbuild.exec.job.Task;
import org.smoothbuild.exec.job.TaskInfo;
import org.smoothbuild.exec.job.VirtualJob;
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
import org.smoothbuild.lang.base.type.Typing;
import org.smoothbuild.lang.expr.AnnotationExpression;
import org.smoothbuild.lang.expr.ArrayLiteralExpression;
import org.smoothbuild.lang.expr.BlobLiteralExpression;
import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.IntLiteralExpression;
import org.smoothbuild.lang.expr.ParameterReferenceExpression;
import org.smoothbuild.lang.expr.ReferenceExpression;
import org.smoothbuild.lang.expr.SelectExpression;
import org.smoothbuild.lang.expr.StringLiteralExpression;
import org.smoothbuild.util.Scope;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class JobCreator {
  private final Definitions definitions;
  private final TypeToSpecConverter toSpecConverter;
  private final MethodLoader methodLoader;
  private final Typing typing;
  private final Map<Class<?>, Handler<?>> map;

  @Inject
  public JobCreator(Definitions definitions, TypeToSpecConverter toSpecConverter,
      MethodLoader methodLoader, Typing typing) {
    this(definitions, toSpecConverter, methodLoader, typing, ImmutableMap.of());
  }

  // Visible for testing
  JobCreator(Definitions definitions, TypeToSpecConverter toSpecConverter,
      MethodLoader methodLoader, Typing typing, Map<Class<?>, Handler<?>> additionalHandlers) {
    this.definitions = definitions;
    this.toSpecConverter = toSpecConverter;
    this.methodLoader = methodLoader;
    this.typing = typing;
    this.map = constructHandlers(additionalHandlers);
  }

  private ImmutableMap<Class<?>, Handler<?>> constructHandlers(
      Map<Class<?>, Handler<?>> additionalHandlers) {
    return ImmutableMap.<Class<?>, Handler<?>>builder()
        .put(AnnotationExpression.class,
            new Handler<>(this::nativeLazy, this::nativeEager))
        .put(CallExpression.class,
            new Handler<>(this::callLazy, this::callEager))
        .put(SelectExpression.class,
            new Handler<>(this::selectLazy, this::selectReadEager))
        .put(ParameterReferenceExpression.class,
            new Handler<>(this::paramReferenceLazy, this::paramReferenceLazy))
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

  public Job jobFor(Scope<Job> scope, Expression expression, boolean eager) {
    return handlerFor(expression).job(eager).apply(scope, expression);
  }

  public Job eagerJobFor(Scope<Job> scope, Expression expression) {
    return handlerFor(expression).eagerJob().apply(scope, expression);
  }

  private Job lazyJobFor(Scope<Job> scope, Expression expression) {
    return handlerFor(expression).lazyJob().apply(scope, expression);
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

  private Job nativeLazy(Scope<Job> scope, AnnotationExpression nativ) {
    return nativeLazyJob(nativ);
  }

  private Job nativeEager(Scope<Job> scope, AnnotationExpression nativ) {
    return stringEagerJob(nativ.path());
  }

  private Job nativeLazyJob(AnnotationExpression nativ) {
    return stringLazyJob(nativ.path());
  }

  private Job nativeEagerJob(AnnotationExpression nativ) {
    return stringEagerJob(nativ.path());
  }

  // CallExpression

  private Job callLazy(Scope<Job> scope, CallExpression call) {
    return callJob(scope, call, false);
  }

  private Job callEager(Scope<Job> scope, CallExpression call) {
    return callJob(scope, call, true);
  }

  private Job callJob(Scope<Job> scope, CallExpression call, boolean eager) {
    var function = jobFor(scope, call.function(), eager);
    var arguments = argumentLazyJobs(scope, function, call.arguments(), call.location());
    Location location = call.location();
    var variables = inferVariablesInFunctionCall(function, arguments);
    return callJob(scope, function, arguments, location, variables, eager);
  }

  private Job callJob(Scope<Job> scope, Job function, List<Job> arguments,
      Location location, BoundsMap variables, boolean eager) {
    if (eager) {
      return callEagerJob(scope, function, arguments, location, variables);
    } else {
      var functionType = (FunctionType) function.type();
      var actualResultType = typing.mapVariables(functionType.resultType(), variables, lower());
      return new LazyJob(actualResultType, location,
          () -> callEagerJob(scope, function, arguments, location, variables));
    }
  }

  public Job callEagerJob(Scope<Job> scope, Job function, List<Job> arguments,
      Location location) {
    var variables = inferVariablesInFunctionCall(function, arguments);
    return callEagerJob(scope, function, arguments, location, variables);
  }

  private Job callEagerJob(Scope<Job> scope, Job function, List<Job> arguments,
      Location location, BoundsMap variables) {
    var functionType = (FunctionType) function.type();
    var actualResultType = typing.mapVariables(functionType.resultType(), variables, lower());
    return new ApplyJob(
        actualResultType, function, arguments, location, variables, scope, JobCreator.this);
  }

  private BoundsMap inferVariablesInFunctionCall(Job function, List<Job> arguments) {
    var functionType = (FunctionType) function.type();
    var argumentTypes = map(arguments, Job::type);
    return typing.inferVariableBounds(functionType.parameterTypes(), argumentTypes, lower());
  }

  private List<Job> argumentLazyJobs(Scope<Job> scope, Job function,
      List<Optional<Expression>> arguments, Location location) {
    var builder = ImmutableList.<Job>builder();
    for (int i = 0; i < arguments.size(); i++) {
      builder.add(arguments.get(i)
          .map(a -> lazyJobFor(scope, a))
          .orElse(defaultArgumentLazyJob(function, i, scope, location)));
    }
    return builder.build();
  }

  private Job defaultArgumentLazyJob(Job function, int index, Scope<Job> scope,
      Location location) {
    var type = ((FunctionType) function.type()).parameterTypes().get(index);
    return new LazyJob(type, location, () -> new DefaultArgumentJob(type,
        "default parameter value", function, index, location, scope, JobCreator.this));
  }

  // FieldReadExpression

  private Job selectLazy(Scope<Job> scope, SelectExpression select) {
    var type = select.field().type();
    var location = select.location();
    return new LazyJob(type, location, () -> selectReadEager(scope, select, type));
  }

  private Job selectReadEager(Scope<Job> scope, SelectExpression select) {
    var type = select.field().type();
    return selectReadEager(scope, select, type);
  }

  private Job selectReadEager(Scope<Job> scope, SelectExpression expression, Type type) {
    var structType = (StructType) expression.expression().type();
    var name = expression.field().name().get();
    var algorithm = new ReadRecItemAlgorithm(
        structType.fieldIndex(name), toSpecConverter.visit(type));
    var dependencies = list(eagerJobFor(scope, expression.expression()));
    var info = new TaskInfo(SELECT, "." + name, expression.location());
    return new Task(type, dependencies, info, algorithm);
  }

  // ParameterReferenceExpression

  private Job paramReferenceLazy(Scope<Job> scope,
      ParameterReferenceExpression parameterReference) {
    return scope.get(parameterReference.name());
  }

  // ReferenceExpression

  private Job referenceLazy(Scope<Job> scope, ReferenceExpression reference) {
    var type = reference.type().strip();
    return new LazyJob(type, reference.location(),
        () -> referenceEager(scope, reference, type));
  }

  private Job referenceEager(Scope<Job> scope, ReferenceExpression reference) {
    return referenceEager(scope, reference, reference.type().strip());
  }

  private Job referenceEager(Scope<Job> scope, ReferenceExpression reference, Type type) {
    var referencable = definitions.referencables().get(reference.name());
    var module = definitions.modules().get(referencable.modulePath());
    var algorithm = new ReferenceAlgorithm(referencable, module, toSpecConverter.functionSpec());
    var info = new TaskInfo(REFERENCE, ":" + referencable.name(), reference.location());
    var job = new Task(type, list(), info, algorithm);
    if (referencable instanceof Value) {
      return new ApplyJob(
          type, job, list(), reference.location(), boundsMap(), scope, JobCreator.this);
    } else {
      return job;
    }
  }

  // ArrayLiteralExpression

  private Job arrayLazy(Scope<Job> scope, ArrayLiteralExpression arrayLiteral) {
    var elements = map(arrayLiteral.elements(), e -> lazyJobFor(scope, e));
    var actualType = arrayType(elements).orElse(arrayLiteral.type());

    return new LazyJob(actualType, arrayLiteral.location(),
        () -> arrayEager(arrayLiteral, elements, actualType));
  }

  private Job arrayEager(Scope<Job> scope, ArrayLiteralExpression arrayLiteral) {
    var elements = map(arrayLiteral.elements(), e -> eagerJobFor(scope, e));
    var actualType = arrayType(elements).orElse(arrayLiteral.type());
    return arrayEager(arrayLiteral, elements, actualType);
  }

  private Optional<ArrayType> arrayType(List<Job> elements) {
    return elements
        .stream()
        .map(Job::type)
        .reduce((typeA, typeB) -> typing.merge(typeA, typeB, typing.upper()))
        .map(Types::arrayT);
  }

  private Job arrayEager(ArrayLiteralExpression expression, List<Job> elements,
      ArrayType actualType) {
    var convertedElements = map(elements, e -> convertIfNeededEagerJob(actualType.elemType(), e));
    var info = new TaskInfo(LITERAL, "[]", expression.location());
    return arrayEager(actualType, convertedElements, info);
  }

  public Job arrayEager(ArrayType type, ImmutableList<Job> elements, TaskInfo info) {
    var algorithm = new CreateArrayAlgorithm(toSpecConverter.visit(type));
    return new Task(type, elements, info, algorithm);
  }

  // BlobLiteralExpression

  private Job blobLazy(Scope<Job> scope, BlobLiteralExpression blobLiteral) {
    return new LazyJob(blobT(), blobLiteral.location(), () -> blogEagerJob(blobLiteral));
  }

  private Job blobEager(Scope<Job> scope, BlobLiteralExpression expression) {
    return blogEagerJob(expression);
  }

  private Job blogEagerJob(BlobLiteralExpression expression) {
    var blobSpec = toSpecConverter.visit(blobT());
    var algorithm = new FixedBlobAlgorithm(blobSpec, expression.byteString());
    var info = new TaskInfo(LITERAL, algorithm.shortedLiteral(), expression.location());
    return new Task(blobT(), list(), info, algorithm);
  }

  // IntLiteralExpression

  private Job intLazy(Scope<Job> scope, IntLiteralExpression intLiteral) {
    return new LazyJob(intT(), intLiteral.location(), () -> intEager(intLiteral));
  }

  private Job intEager(Scope<Job> scope, IntLiteralExpression intLiteral) {
    return intEager(intLiteral);
  }

  private Job intEager(IntLiteralExpression expression) {
    var intSpec = toSpecConverter.visit(intT());
    var bigInteger = expression.bigInteger();
    var algorithm = new FixedIntAlgorithm(intSpec, bigInteger);
    var info = new TaskInfo(LITERAL, bigInteger.toString(), expression.location());
    return new Task(intT(), list(), info, algorithm);
  }

  // StringLiteralExpression

  private Job stringLazy(Scope<Job> scope, StringLiteralExpression stringLiteral) {
    return stringLazyJob(stringLiteral);
  }

  private Job stringEager(Scope<Job> scope, StringLiteralExpression stringLiteral) {
    return stringEagerJob(stringLiteral);
  }

  private Job stringLazyJob(StringLiteralExpression stringLiteral) {
    return new LazyJob(stringT(), stringLiteral.location(),
        () -> stringEagerJob(stringLiteral));
  }

  private Job stringEagerJob(StringLiteralExpression stringLiteral) {
    var stringType = toSpecConverter.visit(stringT());
    var algorithm = new FixedStringAlgorithm(stringType, stringLiteral.string());
    var name = algorithm.shortedString();
    var info = new TaskInfo(LITERAL, name, stringLiteral.location());
    return new Task(stringT(), list(), info, algorithm);
  }

  // helper methods

  public Job defaultArgumentEagerJob(Scope<Job> scope, String functionName, int index) {
    var function = (Function) definitions.referencables().get(functionName);
    var defaultArgumentExpression = function.parameters().get(index).defaultValue().get();
    return eagerJobFor(scope, defaultArgumentExpression);
  }

  public Job evaluateLambdaEagerJob(Scope<Job> scope, BoundsMap variables,
      Type actualResultType, String name, List<Job> arguments, Location location) {
    var referencable = definitions.referencables().get(name);
    if (referencable instanceof Value value) {
      return valueEagerJob(scope, value, location);
    } else if (referencable instanceof DefinedFunction definedFunction) {
      return definedFunctionEagerJob(scope, actualResultType, definedFunction, arguments, location);
    } else if (referencable instanceof NativeFunction nativeFunction) {
      return callNativeFunctionEagerJob(scope, arguments, nativeFunction, nativeFunction.nativ(),
          variables, actualResultType, location);
    } else if (referencable instanceof IfFunction) {
      return new IfJob(actualResultType, arguments, location);
    } else if (referencable instanceof MapFunction) {
      return new MapJob(actualResultType, arguments, location, scope, this);
    } else if (referencable instanceof Constructor constructor) {
      var resultType = constructor.type().resultType();
      var recSpec = (RecSpec) toSpecConverter.visit(resultType);
      return constructorCallEagerJob(resultType, recSpec, constructor.extendedName(),
          arguments, location);
    } else {
      throw new IllegalArgumentException(
          "Unexpected case: " + referencable.getClass().getCanonicalName());
    }
  }

  public Job commandLineValueEagerJob(Value value) {
    return valueEagerJob(new Scope<>(Map.of()), value, commandLineLocation());
  }

  private Job valueEagerJob(Scope<Job> scope, Value value, Location location) {
    if (value instanceof DefinedValue definedValue) {
      return definedValueEagerJob(scope, definedValue, location);
    } else if (value instanceof NativeValue nativeValue) {
      return callNativeValueEagerJob(nativeValue, location);
    } else {
      throw new IllegalArgumentException(
          "Unexpected case: " + value.getClass().getCanonicalName());
    }
  }

  private Job definedValueEagerJob(Scope<Job> scope, DefinedValue definedValue,
      Location location) {
    var job = eagerJobFor(scope, definedValue.body());
    var convertedTask = convertIfNeededEagerJob(definedValue.type(), job);
    var taskInfo = new TaskInfo(VALUE, definedValue.extendedName(), location);
    return new VirtualJob(convertedTask, taskInfo);
  }

  private Job callNativeValueEagerJob(NativeValue nativeValue, Location location) {
    AnnotationExpression nativ = nativeValue.nativ();
    var algorithm = new CallNativeAlgorithm(
        methodLoader, toSpecConverter.visit(nativeValue.type()), nativeValue, nativ.isPure());
    var nativeCode = nativeEagerJob(nativ);
    var info = new TaskInfo(VALUE, nativeValue.extendedName(), location);
    return new Task(nativeValue.type(), list(nativeCode), info, algorithm
    );
  }

  private Job definedFunctionEagerJob(Scope<Job> scope, Type actualResultType,
      DefinedFunction function, List<Job> arguments, Location location) {
    var newScope = new Scope<>(scope, nameToArgumentMap(function.parameters(), arguments));
    var body = eagerJobFor(newScope, function.body());
    var convertedTask = convertIfNeededEagerJob(actualResultType, body);
    var taskInfo = new TaskInfo(CALL, function.extendedName(), location);
    return new VirtualJob(convertedTask, taskInfo);
  }

  private static Map<String, Job> nameToArgumentMap(List<Item> names, List<Job> arguments) {
    var mapEntries = zip(names, arguments, (n, a) -> Map.entry(n.name(), a));
    return ImmutableMap.copyOf(mapEntries);
  }

  private Job callNativeFunctionEagerJob(Scope<Job> scope, List<Job> arguments,
      NativeFunction function, AnnotationExpression nativ, BoundsMap variables,
      Type actualResultType, Location location) {
    var algorithm = new CallNativeAlgorithm(
        methodLoader, toSpecConverter.visit(actualResultType), function, nativ.isPure());
    var dependencies = concat(
        eagerJobFor(scope, nativ),
        convertedArgumentEagerJob(arguments, function, variables));
    var info = new TaskInfo(CALL, function.extendedName(), location);
    return new Task(actualResultType, dependencies, info, algorithm
    );
  }

  private ImmutableList<Job> convertedArgumentEagerJob(
      List<Job> arguments, NativeFunction function, BoundsMap variables) {
    var actualTypes = map(
        function.type().parameterTypes(),
        t -> typing.mapVariables(t, variables, lower()));
    return zip(actualTypes, arguments, this::convertIfNeededEagerJob);
  }

  private Job constructorCallEagerJob(Type resultType, RecSpec recSpec, String name,
      List<Job> arguments, Location location) {
    var algorithm = new CreateRecAlgorithm(recSpec);
    var info = new TaskInfo(CALL, name, location);
    return new Task(resultType, arguments, info, algorithm);
  }

  private Job convertIfNeededEagerJob(Type requiredType, Job job) {
    if (job.type().equals(requiredType)) {
      return job;
    } else {
      return convertEagerJob(requiredType, job);
    }
  }

  private Job convertEagerJob(Type requiredType, Job job) {
    var description = requiredType.name() + "<-" + job.type().name();
    var algorithm = new ConvertAlgorithm(toSpecConverter.visit(requiredType));
    var info = new TaskInfo(CONVERSION, description, job.location());
    return new Task(requiredType, list(job), info, algorithm);
  }

  public record Handler<E>(
      BiFunction<Scope<Job>, E, Job> lazyJob,
      BiFunction<Scope<Job>, E, Job> eagerJob) {
    public BiFunction<Scope<Job>, E, Job> job(boolean eager) {
      return eager ? eagerJob : lazyJob;
    }
  }
}