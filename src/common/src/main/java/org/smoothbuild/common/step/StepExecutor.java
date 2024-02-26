package org.smoothbuild.common.step;

import com.google.inject.Injector;
import com.google.inject.Key;
import jakarta.inject.Inject;
import java.util.function.Function;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.Try;
import org.smoothbuild.common.step.Step.ComposedStep;
import org.smoothbuild.common.step.Step.NamedStep;

public class StepExecutor {
  public static final String NAMES_SEPARATOR = "::";
  private final Injector injector;
  private final String fullName;

  @Inject
  public StepExecutor(Injector injector) {
    this(injector, "");
  }

  private StepExecutor(Injector injector, String header) {
    this.injector = injector;
    this.fullName = header;
  }

  public <T, R> Maybe<R> execute(Step<T, R> step, T argument, StepReporter reporter) {
    return switch (step) {
      case Step.ComposedStep<T, ?, R> c -> composedStep(c, argument, reporter);
      case Step.FunctionStep<T, R> l -> function(l.function(), argument, reporter);
      case Step.FunctionKeyStep<T, R> f -> functionKey(f.key(), argument, reporter);
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
    var name = fullName + NAMES_SEPARATOR + namedStep.name();
    reporter.startNewPhase(name);
    return new StepExecutor(injector, name).execute(namedStep.step(), argument, reporter);
  }

  private <T, R> Maybe<R> functionKey(
      Key<? extends Function<T, Try<R>>> key, T argument, StepReporter reporter) {
    var function = injector.getInstance(key);
    return function(function, argument, reporter);
  }

  private <T, R> Maybe<R> maybeFunctionKey(Key<? extends MaybeFunction<T, R>> key, T argument) {
    return injector.getInstance(key).apply(argument);
  }

  private <T, R> Maybe<R> function(
      Function<T, Try<R>> function, T argument, StepReporter reporter) {
    var result = function.apply(argument);
    result.logs().forEach(reporter::report);
    return result.toMaybe();
  }

  private <R, T> Maybe<R> factory(
      StepFactory<T, R> stepFactory, T argument, StepReporter reporter) {
    return execute(stepFactory.create(argument), null, reporter);
  }
}
