package org.smoothbuild.run.step;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.out.log.Maybe.maybe;

import java.util.function.Function;

import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.run.step.Step.ComposedStep;
import org.smoothbuild.run.step.Step.FactoryStep;
import org.smoothbuild.run.step.Step.FunctionKeyStep;
import org.smoothbuild.run.step.Step.FunctionStep;
import org.smoothbuild.run.step.Step.NamedStep;
import org.smoothbuild.run.step.Step.OptionFunctionKeyStep;

import com.google.inject.Key;

import io.vavr.Tuple;
import io.vavr.Tuple0;
import io.vavr.Tuple2;

public sealed interface Step<T, R>
    permits ComposedStep, FactoryStep, FunctionKeyStep, FunctionStep, NamedStep,
    OptionFunctionKeyStep {
  public default Step<T, R> named(String name) {
    return new NamedStep<>(name, this);
  }

  record NamedStep<T, R>(String name, Step<T, R> step) implements Step<T, R> {}

  public static <T, R> Step<T, R> step(Class<? extends Function<T, Maybe<R>>> clazz) {
    return new FunctionKeyStep<>(Key.get(clazz));
  }

  record FunctionKeyStep<T, R>(Key<? extends Function<T, Maybe<R>>> key) implements Step<T, R> {}

  public static <T> Step<Tuple0, T> constStep(T value) {
    var sanitizedValue = requireNonNull(value);
    return step((Tuple0 t) -> maybe(sanitizedValue));
  }

  public static <T, R> Step<T, R> step(Function<T, Maybe<R>> function) {
    return new FunctionStep<>(function);
  }

  record FunctionStep<T, R>(Function<T, Maybe<R>> function) implements Step<T, R> {}

  public static <T, R> Step<T, R> stepFactory(StepFactory<T, R> stepFactory) {
    return new FactoryStep<>(stepFactory);
  }

  record FactoryStep<T, R>(StepFactory<T, R> stepFactory) implements Step<T, R> {}

  public static <T, R> Step<T, R> optionStep(Class<? extends OptionFunction<T, R>> clazz) {
    return new OptionFunctionKeyStep<>(Key.get(clazz));
  }

  record OptionFunctionKeyStep<T, R>(Key<? extends OptionFunction<T, R>> key) implements Step<T, R> {}

  public default <S> Step<T, Tuple2<R, S>> append(S value) {
    return new ComposedStep<>(this, step((R r) -> maybe(Tuple.of(r, value))));
  }

  public default <S> Step<T, S> then(Step<? super R, S> nextStep) {
    return new ComposedStep<>(this, nextStep);
  }

  record ComposedStep<T, S, R>(Step<T, S> first, Step<? super S, R> second) implements Step<T, R> {}
}
