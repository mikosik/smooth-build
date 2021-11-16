package org.smoothbuild.exec.plan;

import static org.smoothbuild.exec.job.TaskKind.CALL;
import static org.smoothbuild.exec.job.TaskKind.CONVERSION;
import static org.smoothbuild.exec.job.TaskKind.LITERAL;
import static org.smoothbuild.exec.job.TaskKind.REFERENCE;
import static org.smoothbuild.exec.job.TaskKind.SELECT;
import static org.smoothbuild.exec.job.TaskKind.VALUE;
import static org.smoothbuild.lang.base.define.Location.commandLineLocation;
import static org.smoothbuild.lang.base.type.api.BoundsMap.boundsMap;
import static org.smoothbuild.util.collect.Labeled.labeled;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.zip;
import static org.smoothbuild.util.collect.NList.nList;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import javax.inject.Inject;

import org.smoothbuild.db.object.type.val.TupleTypeH;
import org.smoothbuild.exec.algorithm.CallNativeAlgorithm;
import org.smoothbuild.exec.algorithm.ConstructAlgorithm;
import org.smoothbuild.exec.algorithm.ConvertAlgorithm;
import org.smoothbuild.exec.algorithm.FixedBlobAlgorithm;
import org.smoothbuild.exec.algorithm.FixedIntAlgorithm;
import org.smoothbuild.exec.algorithm.FixedStringAlgorithm;
import org.smoothbuild.exec.algorithm.OrderAlgorithm;
import org.smoothbuild.exec.algorithm.ReferenceAlgorithm;
import org.smoothbuild.exec.algorithm.SelectAlgorithm;
import org.smoothbuild.exec.java.MethodLoader;
import org.smoothbuild.exec.job.ApplyJob;
import org.smoothbuild.exec.job.IfJob;
import org.smoothbuild.exec.job.Job;
import org.smoothbuild.exec.job.LazyJob;
import org.smoothbuild.exec.job.MapJob;
import org.smoothbuild.exec.job.Task;
import org.smoothbuild.exec.job.TaskInfo;
import org.smoothbuild.exec.job.VirtualJob;
import org.smoothbuild.lang.base.define.ConstructorS;
import org.smoothbuild.lang.base.define.DefinedFunctionS;
import org.smoothbuild.lang.base.define.DefinedValueS;
import org.smoothbuild.lang.base.define.DefinitionsS;
import org.smoothbuild.lang.base.define.IfFunctionS;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.MapFunctionS;
import org.smoothbuild.lang.base.define.NativeFunctionS;
import org.smoothbuild.lang.base.define.NativeValueS;
import org.smoothbuild.lang.base.define.ValueS;
import org.smoothbuild.lang.base.type.api.BoundsMap;
import org.smoothbuild.lang.base.type.impl.ArrayTypeS;
import org.smoothbuild.lang.base.type.impl.FunctionTypeS;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.base.type.impl.TypingS;
import org.smoothbuild.lang.expr.Annotation;
import org.smoothbuild.lang.expr.BlobS;
import org.smoothbuild.lang.expr.CallS;
import org.smoothbuild.lang.expr.ExprS;
import org.smoothbuild.lang.expr.IntS;
import org.smoothbuild.lang.expr.OrderS;
import org.smoothbuild.lang.expr.ParamRefS;
import org.smoothbuild.lang.expr.RefS;
import org.smoothbuild.lang.expr.SelectS;
import org.smoothbuild.lang.expr.StringS;
import org.smoothbuild.util.Scope;
import org.smoothbuild.util.collect.Labeled;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class JobCreator {
  private final DefinitionsS definitions;
  private final TypeSToTypeOConverter toOTypeConverter;
  private final MethodLoader methodLoader;
  private final TypeFactoryS factory;
  private final TypingS typing;
  private final Map<Class<?>, Handler<?>> map;

  @Inject
  public JobCreator(DefinitionsS definitions, TypeSToTypeOConverter toOTypeConverter,
      MethodLoader methodLoader, TypeFactoryS factory, TypingS typing) {
    this(definitions, toOTypeConverter, methodLoader, factory, typing, ImmutableMap.of());
  }

  // Visible for testing
  JobCreator(DefinitionsS definitions, TypeSToTypeOConverter toOTypeConverter,
      MethodLoader methodLoader, TypeFactoryS factory, TypingS typing,
      Map<Class<?>, Handler<?>> additionalHandlers) {
    this.definitions = definitions;
    this.toOTypeConverter = toOTypeConverter;
    this.methodLoader = methodLoader;
    this.factory = factory;
    this.typing = typing;
    this.map = constructHandlers(additionalHandlers);
  }

  private ImmutableMap<Class<?>, Handler<?>> constructHandlers(
      Map<Class<?>, Handler<?>> additionalHandlers) {
    return ImmutableMap.<Class<?>, Handler<?>>builder()
        .put(Annotation.class,
            new Handler<>(this::nativeLazy, this::nativeEager))
        .put(CallS.class,
            new Handler<>(this::callLazy, this::callEager))
        .put(SelectS.class,
            new Handler<>(this::selectLazy, this::selectEager))
        .put(ParamRefS.class,
            new Handler<>(this::paramReferenceLazy, this::paramReferenceLazy))
        .put(RefS.class,
            new Handler<>(this::referenceLazy, this::referenceEager))
        .put(OrderS.class,
            new Handler<>(this::arrayLazy, this::arrayEager))
        .put(BlobS.class,
            new Handler<>(this::blobLazy, this::blobEager))
        .put(IntS.class,
            new Handler<>(this::intLazy, this::intEager))
        .put(StringS.class,
            new Handler<>(this::stringLazy, this::stringEager))
        .putAll(additionalHandlers)
        .build();
  }

  public Job jobFor(Scope<Labeled<Job>> scope, ExprS expr, boolean eager) {
    return handlerFor(expr).job(eager).apply(scope, expr);
  }

  public Job eagerJobFor(Scope<Labeled<Job>> scope, ExprS expr) {
    return handlerFor(expr).eagerJob().apply(scope, expr);
  }

  private Job lazyJobFor(Scope<Labeled<Job>> scope, ExprS expr) {
    return handlerFor(expr).lazyJob().apply(scope, expr);
  }

  public <T> Handler<T> handlerFor(ExprS expr) {
    @SuppressWarnings("unchecked")
    Handler<T> result = (Handler<T>) map.get(expr.getClass());
    if (result == null) {
      System.out.println("expression.getClass() = " + expr.getClass());
    }
    return result;
  }

  // NativeExpression

  private Job nativeLazy(Scope<Labeled<Job>> scope, Annotation annotation) {
    return nativeLazyJob(annotation);
  }

  private Job nativeEager(Scope<Labeled<Job>> scope, Annotation annotation) {
    return stringEagerJob(annotation.path());
  }

  private Job nativeLazyJob(Annotation annotation) {
    return stringLazyJob(annotation.path());
  }

  private Job nativeEagerJob(Annotation annotation) {
    return stringEagerJob(annotation.path());
  }

  // CallExpression

  private Job callLazy(Scope<Labeled<Job>> scope, CallS call) {
    return callJob(scope, call, false);
  }

  private Job callEager(Scope<Labeled<Job>> scope, CallS call) {
    return callJob(scope, call, true);
  }

  private Job callJob(Scope<Labeled<Job>> scope, CallS call, boolean eager) {
    var function = jobFor(scope, call.function(), eager);
    var arguments = map(call.arguments(), a -> lazyJobFor(scope, a));
    Location location = call.location();
    var variables = inferVariablesInFunctionCall(function, arguments);
    return callJob(scope, function, arguments, location, variables, eager);
  }

  private Job callJob(Scope<Labeled<Job>> scope, Job function, List<Job> arguments,
      Location location, BoundsMap<TypeS> variables, boolean eager) {
    if (eager) {
      return callEagerJob(scope, function, arguments, location, variables);
    } else {
      var functionType = (FunctionTypeS) function.type();
      var actualResultType = typing.mapVariables(functionType.result(), variables, factory.lower());
      return new LazyJob(actualResultType, location,
          () -> callEagerJob(scope, function, arguments, location, variables));
    }
  }

  public Job callEagerJob(Scope<Labeled<Job>> scope, Job function, List<Job> arguments,
      Location location) {
    var variables = inferVariablesInFunctionCall(function, arguments);
    return callEagerJob(scope, function, arguments, location, variables);
  }

  private Job callEagerJob(Scope<Labeled<Job>> scope, Job function, List<Job> arguments,
      Location location, BoundsMap<TypeS> variables) {
    var functionType = (FunctionTypeS) function.type();
    var actualResultType = typing.mapVariables(functionType.result(), variables, factory.lower());
    return new ApplyJob(
        actualResultType, function, arguments, location, variables, scope, JobCreator.this);
  }

  private BoundsMap<TypeS> inferVariablesInFunctionCall(Job function, List<Job> arguments) {
    var functionType = (FunctionTypeS) function.type();
    var argumentTypes = map(arguments, Job::type);
    return typing.inferVariableBounds(functionType.parameters(), argumentTypes, factory.lower());
  }

  // FieldReadExpression

  private Job selectLazy(Scope<Labeled<Job>> scope, SelectS select) {
    var type = select.type();
    var location = select.location();
    return new LazyJob(type, location, () -> selectEager(scope, select, type));
  }

  private Job selectEager(Scope<Labeled<Job>> scope, SelectS select) {
    var type = select.type();
    return selectEager(scope, select, type);
  }

  private Job selectEager(Scope<Labeled<Job>> scope, SelectS expression, TypeS type) {
    var index = expression.index();
    var algorithm = new SelectAlgorithm(index, toOTypeConverter.visit(type));
    var dependencies = list(eagerJobFor(scope, expression.expr()));
    var info = new TaskInfo(SELECT, "." + index, expression.location());
    return new Task(type, dependencies, info, algorithm);
  }

  // ParameterReferenceExpression

  private Job paramReferenceLazy(Scope<Labeled<Job>> scope,
      ParamRefS parameterReference) {
    return scope.get(parameterReference.name()).object();
  }

  // ReferenceExpression

  private Job referenceLazy(Scope<Labeled<Job>> scope, RefS reference) {
    var type = reference.type();
    return new LazyJob(type, reference.location(),
        () -> referenceEager(scope, reference, type));
  }

  private Job referenceEager(Scope<Labeled<Job>> scope, RefS reference) {
    return referenceEager(scope, reference, reference.type());
  }

  private Job referenceEager(Scope<Labeled<Job>> scope, RefS reference, TypeS type) {
    var referencable = definitions.referencables().get(reference.name());
    var module = definitions.modules().get(referencable.modulePath());
    var algorithm = new ReferenceAlgorithm(referencable, module, toOTypeConverter.functionType());
    var info = new TaskInfo(REFERENCE, ":" + referencable.name(), reference.location());
    var job = new Task(type, list(), info, algorithm);
    if (referencable instanceof ValueS) {
      return new ApplyJob(
          type, job, list(), reference.location(), boundsMap(), scope, JobCreator.this);
    } else {
      return job;
    }
  }

  // ArrayLiteralExpression

  private Job arrayLazy(Scope<Labeled<Job>> scope, OrderS arrayLiteral) {
    var elements = map(arrayLiteral.elements(), e -> lazyJobFor(scope, e));
    var actualType = arrayType(elements).orElse(arrayLiteral.type());

    return new LazyJob(actualType, arrayLiteral.location(),
        () -> arrayEager(arrayLiteral, elements, actualType));
  }

  private Job arrayEager(Scope<Labeled<Job>> scope, OrderS arrayLiteral) {
    var elements = map(arrayLiteral.elements(), e -> eagerJobFor(scope, e));
    var actualType = arrayType(elements).orElse(arrayLiteral.type());
    return arrayEager(arrayLiteral, elements, actualType);
  }

  private Optional<ArrayTypeS> arrayType(List<Job> elements) {
    return elements
        .stream()
        .map(Job::type)
        .reduce(typing::mergeUp)
        .map(factory::array);
  }

  private Job arrayEager(OrderS expression, List<Job> elements,
      ArrayTypeS actualType) {
    var convertedElements = map(elements, e -> convertIfNeededEagerJob(actualType.element(), e));
    var info = new TaskInfo(LITERAL, "[]", expression.location());
    return arrayEager(actualType, convertedElements, info);
  }

  public Job arrayEager(ArrayTypeS type, ImmutableList<Job> elements, TaskInfo info) {
    var algorithm = new OrderAlgorithm(toOTypeConverter.visit(type));
    return new Task(type, elements, info, algorithm);
  }

  // BlobLiteralExpression

  private Job blobLazy(Scope<Labeled<Job>> scope, BlobS blobLiteral) {
    return new LazyJob(factory.blob(), blobLiteral.location(), () -> blobEagerJob(blobLiteral));
  }

  private Job blobEager(Scope<Labeled<Job>> scope, BlobS expression) {
    return blobEagerJob(expression);
  }

  private Job blobEagerJob(BlobS expression) {
    var blobType = toOTypeConverter.visit(factory.blob());
    var algorithm = new FixedBlobAlgorithm(blobType, expression.byteString());
    var info = new TaskInfo(LITERAL, algorithm.shortedLiteral(), expression.location());
    return new Task(factory.blob(), list(), info, algorithm);
  }

  // IntLiteralExpression

  private Job intLazy(Scope<Labeled<Job>> scope, IntS intLiteral) {
    return new LazyJob(factory.int_(), intLiteral.location(), () -> intEager(intLiteral));
  }

  private Job intEager(Scope<Labeled<Job>> scope, IntS intLiteral) {
    return intEager(intLiteral);
  }

  private Job intEager(IntS expression) {
    var intType = toOTypeConverter.visit(factory.int_());
    var bigInteger = expression.bigInteger();
    var algorithm = new FixedIntAlgorithm(intType, bigInteger);
    var info = new TaskInfo(LITERAL, bigInteger.toString(), expression.location());
    return new Task(factory.int_(), list(), info, algorithm);
  }

  // StringLiteralExpression

  private Job stringLazy(Scope<Labeled<Job>> scope, StringS stringLiteral) {
    return stringLazyJob(stringLiteral);
  }

  private Job stringEager(Scope<Labeled<Job>> scope, StringS stringLiteral) {
    return stringEagerJob(stringLiteral);
  }

  private Job stringLazyJob(StringS stringLiteral) {
    return new LazyJob(factory.string(), stringLiteral.location(),
        () -> stringEagerJob(stringLiteral));
  }

  private Job stringEagerJob(StringS stringLiteral) {
    var stringType = toOTypeConverter.visit(factory.string());
    var algorithm = new FixedStringAlgorithm(stringType, stringLiteral.string());
    var name = algorithm.shortedString();
    var info = new TaskInfo(LITERAL, name, stringLiteral.location());
    return new Task(factory.string(), list(), info, algorithm);
  }

  // helper methods

  public Job evaluateFunctionEagerJob(Scope<Labeled<Job>> scope, BoundsMap<TypeS> variables,
      TypeS actualResultType, String name, List<Job> arguments, Location location) {
    var referencable = definitions.referencables().get(name);
    if (referencable instanceof ValueS value) {
      return valueEagerJob(scope, value, location);
    } else if (referencable instanceof DefinedFunctionS definedFunction) {
      return definedFunctionEagerJob(scope, actualResultType, definedFunction, arguments, location);
    } else if (referencable instanceof NativeFunctionS nativeFunction) {
      return callNativeFunctionEagerJob(scope, arguments, nativeFunction, nativeFunction.annotation(),
          variables, actualResultType, location);
    } else if (referencable instanceof IfFunctionS) {
      return new IfJob(actualResultType, arguments, location);
    } else if (referencable instanceof MapFunctionS) {
      return new MapJob(actualResultType, arguments, location, scope, this);
    } else if (referencable instanceof ConstructorS constructor) {
      var resultType = constructor.type().result();
      var tupleType = (TupleTypeH) toOTypeConverter.visit(resultType);
      return constructorCallEagerJob(resultType, tupleType, constructor.extendedName(),
          arguments, location);
    } else {
      throw new IllegalArgumentException(
          "Unexpected case: " + referencable.getClass().getCanonicalName());
    }
  }

  public Job commandLineValueEagerJob(ValueS value) {
    return valueEagerJob(new Scope<>(nList()), value, commandLineLocation());
  }

  private Job valueEagerJob(Scope<Labeled<Job>> scope, ValueS value, Location location) {
    if (value instanceof DefinedValueS definedValue) {
      return definedValueEagerJob(scope, definedValue, location);
    } else if (value instanceof NativeValueS nativeValue) {
      return callNativeValueEagerJob(nativeValue, location);
    } else {
      throw new IllegalArgumentException(
          "Unexpected case: " + value.getClass().getCanonicalName());
    }
  }

  private Job definedValueEagerJob(Scope<Labeled<Job>> scope, DefinedValueS definedValue,
      Location location) {
    var job = eagerJobFor(scope, definedValue.body());
    var convertedTask = convertIfNeededEagerJob(definedValue.type(), job);
    var taskInfo = new TaskInfo(VALUE, definedValue.extendedName(), location);
    return new VirtualJob(convertedTask, taskInfo);
  }

  private Job callNativeValueEagerJob(NativeValueS nativeValue, Location location) {
    Annotation annotation = nativeValue.annotation();
    var algorithm = new CallNativeAlgorithm(
        methodLoader, toOTypeConverter.visit(nativeValue.type()), nativeValue, annotation.isPure());
    var nativeCode = nativeEagerJob(annotation);
    var info = new TaskInfo(VALUE, nativeValue.extendedName(), location);
    return new Task(nativeValue.type(), list(nativeCode), info, algorithm
    );
  }

  private Job definedFunctionEagerJob(Scope<Labeled<Job>> scope, TypeS actualResultType,
      DefinedFunctionS function, List<Job> arguments, Location location) {
    var newScope = new Scope<>(scope, namedArguments(function.parameters(), arguments));
    var body = eagerJobFor(newScope, function.body());
    var convertedTask = convertIfNeededEagerJob(actualResultType, body);
    var taskInfo = new TaskInfo(CALL, function.extendedName(), location);
    return new VirtualJob(convertedTask, taskInfo);
  }

  private static NList<Labeled<Job>> namedArguments(
      NList<Item> params, List<Job> arguments) {
    return nList(zip(params, arguments, (p, a) -> labeled(p.name(), a)));
  }

  private Job callNativeFunctionEagerJob(Scope<Labeled<Job>> scope, List<Job> arguments,
      NativeFunctionS function, Annotation annotation, BoundsMap<TypeS> variables,
      TypeS actualResultType, Location location) {
    var algorithm = new CallNativeAlgorithm(
        methodLoader, toOTypeConverter.visit(actualResultType), function, annotation.isPure());
    var dependencies = concat(
        nativeEager(scope, annotation),
        convertedArgumentEagerJob(arguments, function, variables));
    var info = new TaskInfo(CALL, function.extendedName(), location);
    return new Task(actualResultType, dependencies, info, algorithm
    );
  }

  private ImmutableList<Job> convertedArgumentEagerJob(
      List<Job> arguments, NativeFunctionS function, BoundsMap<TypeS> variables) {
    var actualTypes = map(
        function.type().parameters(),
        t -> typing.mapVariables(t, variables, factory.lower()));
    return zip(actualTypes, arguments, this::convertIfNeededEagerJob);
  }

  private Job constructorCallEagerJob(TypeS resultType, TupleTypeH tupleType, String name,
      List<Job> arguments, Location location) {
    var algorithm = new ConstructAlgorithm(tupleType);
    var info = new TaskInfo(CALL, name, location);
    return new Task(resultType, arguments, info, algorithm);
  }

  private Job convertIfNeededEagerJob(TypeS requiredType, Job job) {
    if (job.type().equals(requiredType)) {
      return job;
    } else {
      return convertEagerJob(requiredType, job);
    }
  }

  private Job convertEagerJob(TypeS requiredType, Job job) {
    var description = requiredType.name() + "<-" + job.type().name();
    var algorithm = new ConvertAlgorithm(toOTypeConverter.visit(requiredType));
    var info = new TaskInfo(CONVERSION, description, job.location());
    return new Task(requiredType, list(job), info, algorithm);
  }

  public record Handler<E>(
      BiFunction<Scope<Labeled<Job>>, E, Job> lazyJob,
      BiFunction<Scope<Labeled<Job>>, E, Job> eagerJob) {
    public BiFunction<Scope<Labeled<Job>>, E, Job> job(boolean eager) {
      return eager ? eagerJob : lazyJob;
    }
  }
}
