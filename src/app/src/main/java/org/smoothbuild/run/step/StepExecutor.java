package org.smoothbuild.run.step;

import com.google.inject.Injector;
import com.google.inject.Key;
import io.vavr.control.Option;
import jakarta.inject.Inject;
import java.util.function.Function;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.run.step.Step.ComposedStep;
import org.smoothbuild.run.step.Step.FactoryStep;
import org.smoothbuild.run.step.Step.FunctionKeyStep;
import org.smoothbuild.run.step.Step.FunctionStep;
import org.smoothbuild.run.step.Step.NamedStep;
import org.smoothbuild.run.step.Step.OptionFunctionKeyStep;

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

  public <T, R> Option<R> execute(Step<T, R> step, T argument, Reporter reporter) {
    return switch (step) {
      case ComposedStep<T, ?, R> c -> composedStep(c, argument, reporter);
      case FunctionStep<T, R> l -> function(l.function(), argument, reporter);
      case FunctionKeyStep<T, R> f -> functionKey(f.key(), argument, reporter);
      case OptionFunctionKeyStep<T, R> f -> optionFunctionKey(f.key(), argument);
      case FactoryStep<T, R> i -> factory(i.stepFactory(), argument, reporter);
      case NamedStep<T, R> namedStep -> namedStep(namedStep, argument, reporter);
    };
  }

  private <T, S, R> Option<R> composedStep(
      ComposedStep<T, S, R> composedStep, T argument, Reporter reporter) {
    return execute(composedStep.first(), argument, reporter)
        .flatMap(r -> execute(composedStep.second(), r, reporter));
  }

  private <R, T> Option<R> namedStep(NamedStep<T, R> namedStep, T argument, Reporter reporter) {
    var name = fullName + NAMES_SEPARATOR + namedStep.name();
    reporter.startNewPhase(name);
    return new StepExecutor(injector, name).execute(namedStep.step(), argument, reporter);
  }

  private <T, R> Option<R> functionKey(
      Key<? extends Function<T, Maybe<R>>> key, T argument, Reporter reporter) {
    var function = injector.getInstance(key);
    return function(function, argument, reporter);
  }

  private <T, R> Option<R> optionFunctionKey(Key<? extends OptionFunction<T, R>> key, T argument) {
    return injector.getInstance(key).apply(argument);
  }

  private <T, R> Option<R> function(Function<T, Maybe<R>> function, T argument, Reporter reporter) {
    var result = function.apply(argument);
    var errors = result.logs().toList();
    if (!errors.isEmpty()) {
      errors.forEach(reporter::report);
    }
    return Option.ofOptional(result.valueOptional());
  }

  private <R, T> Option<R> factory(StepFactory<T, R> stepFactory, T argument, Reporter reporter) {
    return execute(stepFactory.create(argument), null, reporter);
  }
}
