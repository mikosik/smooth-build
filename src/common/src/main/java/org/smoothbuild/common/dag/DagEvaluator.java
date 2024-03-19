package org.smoothbuild.common.dag;

import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.MappingReporter.labelPrefixingReporter;
import static org.smoothbuild.common.log.report.Report.report;

import com.google.inject.Injector;
import jakarta.inject.Inject;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.collect.Maybe.Some;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.log.report.Reporter;

public class DagEvaluator {
  private final Injector injector;

  @Inject
  public DagEvaluator(Injector injector) {
    this.injector = injector;
  }

  public <V> Maybe<V> evaluate(Dag<V> dag, Reporter reporter) {
    return switch (dag) {
      case Application0<V> application -> evaluateApplication0(application, reporter);
      case Application1<?, V> application -> evaluateApplication1(application, reporter);
      case Application2<?, ?, V> application -> evaluateApplication2(application, reporter);
      case Chain<V> chain -> evaluateChain(chain, reporter);
      case Evaluation<V> evaluation -> evaluateEvaluation(evaluation, reporter);
      case Injection<V> injection -> evaluateInjection(injection, reporter);
      case MaybeApplication<?, V> application -> evaluateMaybeApplication(application, reporter);
      case Prefix<V> prefix -> evaluatePrefix(prefix, reporter);
      case Value<V> value -> evaluateValue(value);
    };
  }

  private <V> Maybe<V> evaluateApplication0(Application0<V> application, Reporter reporter) {
    var maybeFunction = evaluate(application.function(), reporter);
    if (maybeFunction.isSome()) {
      Try<V> result = maybeFunction.get().apply();
      reporter.report(report(label(), "", EXECUTION, result.logs()));
      return result.toMaybe();
    } else {
      return none();
    }
  }

  private <A, V> Maybe<V> evaluateApplication1(Application1<A, V> application, Reporter reporter) {
    var argument = evaluate(application.argument(), reporter);
    var function = evaluate(application.function(), reporter);
    if (argument.isSome() && function.isSome()) {
      Try<V> result = function.get().apply(argument.get());
      reporter.report(report(label(), "", EXECUTION, result.logs()));
      return result.toMaybe();
    }
    return none();
  }

  private <A, B, V> Maybe<V> evaluateApplication2(
      Application2<A, B, V> application, Reporter reporter) {
    var maybeArgument1 = evaluate(application.argument1(), reporter);
    var maybeArgument2 = evaluate(application.argument2(), reporter);
    var maybeFunction = evaluate(application.function(), reporter);
    if (maybeFunction.isSome() && maybeArgument1.isSome() && maybeArgument2.isSome()) {
      Try<V> result = maybeFunction.get().apply(maybeArgument1.get(), maybeArgument2.get());
      reporter.report(report(label(), "", EXECUTION, result.logs()));
      return result.toMaybe();
    } else {
      return none();
    }
  }

  private <V> Maybe<V> evaluateChain(Chain<V> chain, Reporter reporter) {
    return evaluate(chain.first(), reporter).flatMap(v -> evaluate(chain.second(), reporter));
  }

  private <V> Maybe<V> evaluateEvaluation(Evaluation<V> evaluation, Reporter reporter) {
    Dag<Dag<V>> dag = evaluation.dag();
    Maybe<Dag<V>> inflatedNode = evaluate(dag, reporter);
    return inflatedNode.flatMap(n -> evaluate(n, reporter));
  }

  private <V> Maybe<V> evaluateInjection(Injection<V> injection, Reporter reporter) {
    return some(injector.getInstance(injection.key()));
  }

  private <A, V> Maybe<V> evaluateMaybeApplication(
      MaybeApplication<A, V> application, Reporter reporter) {
    var argument = evaluate(application.argument(), reporter);
    var function = evaluate(application.function(), reporter);
    if (argument.isSome() && function.isSome()) {
      return function.get().apply(argument.get());
    }
    return none();
  }

  private <V> Maybe<V> evaluatePrefix(Prefix<V> prefix, Reporter reporter) {
    var prefixingReporter = labelPrefixingReporter(reporter, prefix.label());
    return evaluate(prefix.dag(), prefixingReporter);
  }

  private static <V> Some<V> evaluateValue(Value<V> value) {
    return some(value.value());
  }
}
