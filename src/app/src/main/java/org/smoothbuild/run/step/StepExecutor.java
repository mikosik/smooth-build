package org.smoothbuild.run.step;

import com.google.inject.Injector;
import com.google.inject.Key;
import jakarta.inject.Inject;
import java.util.function.Function;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.out.log.Try;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.run.step.Step.ComposedStep;
import org.smoothbuild.run.step.Step.FactoryStep;
import org.smoothbuild.run.step.Step.FunctionKeyStep;
import org.smoothbuild.run.step.Step.FunctionStep;
import org.smoothbuild.run.step.Step.NamedStep;

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

  public <T, R> Maybe<R> execute(Step<T, R> step, T argument, Reporter reporter) {
    return switch (step) {
      case ComposedStep<T, ?, R> c -> composedStep(c, argument, reporter);
      case FunctionStep<T, R> l -> function(l.function(), argument, reporter);
      case FunctionKeyStep<T, R> f -> functionKey(f.key(), argument, reporter);
      case Step.MaybeFunctionKeyStep<T, R> f -> maybeFunctionKey(f.key(), argument);
      case FactoryStep<T, R> i -> factory(i.stepFactory(), argument, reporter);
      case NamedStep<T, R> namedStep -> namedStep(namedStep, argument, reporter);
    };
  }

  private <T, S, R> Maybe<R> composedStep(
      ComposedStep<T, S, R> composedStep, T argument, Reporter reporter) {
    return execute(composedStep.first(), argument, reporter)
        .flatMap(r -> execute(composedStep.second(), r, reporter));
  }

  private <R, T> Maybe<R> namedStep(NamedStep<T, R> namedStep, T argument, Reporter reporter) {
    var name = fullName + NAMES_SEPARATOR + namedStep.name();
    reporter.startNewPhase(name);
    return new StepExecutor(injector, name).execute(namedStep.step(), argument, reporter);
  }

  private <T, R> Maybe<R> functionKey(
      Key<? extends Function<T, Try<R>>> key, T argument, Reporter reporter) {
    var function = injector.getInstance(key);
    return function(function, argument, reporter);
  }

  private <T, R> Maybe<R> maybeFunctionKey(Key<? extends MaybeFunction<T, R>> key, T argument) {
    return injector.getInstance(key).apply(argument);
  }

  private <T, R> Maybe<R> function(Function<T, Try<R>> function, T argument, Reporter reporter) {
    var result = function.apply(argument);
    var errors = result.logs().toList();
    if (!errors.isEmpty()) {
      errors.forEach(reporter::report);
    }
    return result.toMaybe();
  }

  private <R, T> Maybe<R> factory(StepFactory<T, R> stepFactory, T argument, Reporter reporter) {
    return execute(stepFactory.create(argument), null, reporter);
  }
}
