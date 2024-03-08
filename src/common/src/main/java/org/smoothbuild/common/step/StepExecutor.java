package org.smoothbuild.common.step;

import static org.smoothbuild.common.log.Label.label;
import static org.smoothbuild.common.log.ResultSource.EXECUTION;

import com.google.inject.Injector;
import com.google.inject.Key;
import jakarta.inject.Inject;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.step.Step.ComposedStep;
import org.smoothbuild.common.step.Step.NamedStep;

public class StepExecutor {
  private final Injector injector;

  @Inject
  public StepExecutor(Injector injector) {
    this.injector = injector;
  }

  public <T, R> Maybe<R> execute(Step<T, R> step, T argument, StepReporter reporter) {
    return switch (step) {
      case Step.ComposedStep<T, ?, R> c -> composedStep(c, argument, reporter);
      case Step.TryFunctionStep<T, R> l -> tryFunction(l.function(), argument, reporter);
      case Step.TryFunctionKeyStep<T, R> f -> tryFunctionKey(f.key(), argument, reporter);
      case Step.MaybeFunctionKeyStep<T, R> f -> maybeFunctionKey(f.key(), argument);
      case Step.FactoryStep<T, R> i -> factory(i.stepFactory(), argument, reporter);
      case Step.NamedStep<T, R> namedStep -> namedStep(namedStep, argument, reporter);
    };
  }

  private <T, S, R> Maybe<R> composedStep(
      ComposedStep<T, S, R> composedStep, T argument, StepReporter reporter) {
    return execute(composedStep.first(), argument, reporter)
        .flatMap(r -> execute(composedStep.second(), r, reporter));
  }

  private <R, T> Maybe<R> namedStep(NamedStep<T, R> namedStep, T argument, StepReporter reporter) {
    var newReporter = new PrefixingStepReporter(reporter, label(namedStep.name()));
    return execute(namedStep.step(), argument, newReporter);
  }

  private <T, R> Maybe<R> tryFunctionKey(
      Key<? extends TryFunction<T, R>> key, T argument, StepReporter reporter) {
    var function = injector.getInstance(key);
    return tryFunction(function, argument, reporter);
  }

  private <T, R> Maybe<R> maybeFunctionKey(Key<? extends MaybeFunction<T, R>> key, T argument) {
    return injector.getInstance(key).apply(argument);
  }

  private <T, R> Maybe<R> tryFunction(
      TryFunction<T, R> function, T argument, StepReporter reporter) {
    var result = function.apply(argument);
    reporter.report(label(), "", EXECUTION, result.logs());
    return result.toMaybe();
  }

  private <R, T> Maybe<R> factory(
      StepFactory<T, R> stepFactory, T argument, StepReporter reporter) {
    return execute(stepFactory.create(argument), null, reporter);
  }
}
