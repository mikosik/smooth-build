package org.smoothbuild.common.plan;

import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;

import com.google.inject.Injector;
import jakarta.inject.Inject;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.collect.Maybe.Some;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.log.report.Trace;

public class PlanExecutor {
  private final Injector injector;
  private final Reporter reporter;

  @Inject
  public PlanExecutor(Injector injector, Reporter reporter) {
    this.injector = injector;
    this.reporter = reporter;
  }

  public <V> Maybe<V> evaluate(Plan<V> plan) {
    return switch (plan) {
      case Application0<V> application -> evaluateApplication0(application);
      case Application1<?, V> application -> evaluateApplication1(application);
      case Application2<?, ?, V> application -> evaluateApplication2(application);
      case Chain<V> chain -> evaluateChain(chain);
      case Evaluation<V> evaluation -> evaluateEvaluation(evaluation);
      case Injection<V> injection -> evaluateInjection(injection);
      case MaybeApplication<?, V> application -> evaluateMaybeApplication(application);
      case Value<V> value -> evaluateValue(value);
    };
  }

  private <V> Maybe<V> evaluateApplication0(Application0<V> application) {
    var maybeFunction = evaluate(application.function());
    if (maybeFunction.isSome()) {
      TryFunction0<V> function = maybeFunction.get();
      Try<V> result = function.apply();
      reporter.report(report(function.label(), new Trace(), EXECUTION, result.logs()));
      return result.toMaybe();
    } else {
      return none();
    }
  }

  private <A, V> Maybe<V> evaluateApplication1(Application1<A, V> application) {
    var mabyeFunction = evaluate(application.function());
    var maybeArgument = evaluate(application.argument());
    if (maybeArgument.isSome() && mabyeFunction.isSome()) {
      var function = mabyeFunction.get();
      Try<V> result = function.apply(maybeArgument.get());
      reporter.report(report(function.label(), new Trace(), EXECUTION, result.logs()));
      return result.toMaybe();
    }
    return none();
  }

  private <A, B, V> Maybe<V> evaluateApplication2(Application2<A, B, V> application) {
    var maybeFunction = evaluate(application.function());
    var maybeArgument1 = evaluate(application.argument1());
    var maybeArgument2 = evaluate(application.argument2());
    if (maybeFunction.isSome() && maybeArgument1.isSome() && maybeArgument2.isSome()) {
      var function = maybeFunction.get();
      Try<V> result = function.apply(maybeArgument1.get(), maybeArgument2.get());
      reporter.report(report(function.label(), new Trace(), EXECUTION, result.logs()));
      return result.toMaybe();
    } else {
      return none();
    }
  }

  private <V> Maybe<V> evaluateChain(Chain<V> chain) {
    return evaluate(chain.first()).flatMap(v -> evaluate(chain.second()));
  }

  private <V> Maybe<V> evaluateEvaluation(Evaluation<V> evaluation) {
    Plan<Plan<V>> plan = evaluation.plan();
    Maybe<Plan<V>> inflatedNode = evaluate(plan);
    return inflatedNode.flatMap(n -> evaluate(n));
  }

  private <V> Maybe<V> evaluateInjection(Injection<V> injection) {
    return some(injector.getInstance(injection.key()));
  }

  private <A, V> Maybe<V> evaluateMaybeApplication(MaybeApplication<A, V> application) {
    var argument = evaluate(application.argument());
    var function = evaluate(application.function());
    if (argument.isSome() && function.isSome()) {
      return function.get().apply(argument.get());
    }
    return none();
  }

  private static <V> Some<V> evaluateValue(Value<V> value) {
    return some(value.value());
  }
}
