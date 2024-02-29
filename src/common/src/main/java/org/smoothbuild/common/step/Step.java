package org.smoothbuild.common.step;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.common.log.Try.success;
import static org.smoothbuild.common.tuple.Tuples.tuple;

import com.google.inject.Key;
import org.smoothbuild.common.step.Step.ComposedStep;
import org.smoothbuild.common.step.Step.FactoryStep;
import org.smoothbuild.common.step.Step.MaybeFunctionKeyStep;
import org.smoothbuild.common.step.Step.NamedStep;
import org.smoothbuild.common.step.Step.TryFunctionKeyStep;
import org.smoothbuild.common.step.Step.TryFunctionStep;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.common.tuple.Tuple2;

public sealed interface Step<T, R>
    permits ComposedStep,
        FactoryStep,
        TryFunctionKeyStep,
        TryFunctionStep,
        MaybeFunctionKeyStep,
        NamedStep {
  public default Step<T, R> named(String name) {
    return new NamedStep<>(name, this);
  }

  record NamedStep<T, R>(String name, Step<T, R> step) implements Step<T, R> {}

  public static <T, R> Step<T, R> tryStep(Class<? extends TryFunction<T, R>> clazz) {
    return new TryFunctionKeyStep<>(Key.get(clazz));
  }

  record TryFunctionKeyStep<T, R>(Key<? extends TryFunction<T, R>> key) implements Step<T, R> {}

  public static <T> Step<Tuple0, T> constStep(T value) {
    var sanitizedValue = requireNonNull(value);
    return tryStep((Tuple0 t) -> success(sanitizedValue));
  }

  public static <T, R> Step<T, R> tryStep(TryFunction<T, R> function) {
    return new TryFunctionStep<>(function);
  }

  record TryFunctionStep<T, R>(TryFunction<T, R> function) implements Step<T, R> {}

  public static <T, R> Step<T, R> stepFactory(StepFactory<T, R> stepFactory) {
    return new FactoryStep<>(stepFactory);
  }

  record FactoryStep<T, R>(StepFactory<T, R> stepFactory) implements Step<T, R> {}

  public static <T, R> Step<T, R> maybeStep(Class<? extends MaybeFunction<T, R>> clazz) {
    return new MaybeFunctionKeyStep<>(Key.get(clazz));
  }

  record MaybeFunctionKeyStep<T, R>(Key<? extends MaybeFunction<T, R>> key) implements Step<T, R> {}

  public default <S> Step<T, Tuple2<R, S>> append(S value) {
    return new ComposedStep<>(this, tryStep((R r) -> success(tuple(r, value))));
  }

  public default <S> Step<T, S> then(Step<? super R, S> nextStep) {
    return new ComposedStep<>(this, nextStep);
  }

  record ComposedStep<T, S, R>(Step<T, S> first, Step<? super S, R> second) implements Step<T, R> {}
}
