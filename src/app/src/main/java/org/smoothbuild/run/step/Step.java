package org.smoothbuild.run.step;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.common.log.Try.success;
import static org.smoothbuild.common.tuple.Tuples.tuple;

import com.google.inject.Key;
import java.util.function.Function;
import org.smoothbuild.common.log.Try;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.common.tuple.Tuple2;
import org.smoothbuild.run.step.Step.ComposedStep;
import org.smoothbuild.run.step.Step.FactoryStep;
import org.smoothbuild.run.step.Step.FunctionKeyStep;
import org.smoothbuild.run.step.Step.FunctionStep;
import org.smoothbuild.run.step.Step.MaybeFunctionKeyStep;
import org.smoothbuild.run.step.Step.NamedStep;

public sealed interface Step<T, R>
    permits ComposedStep,
        FactoryStep,
        FunctionKeyStep,
        FunctionStep,
        NamedStep,
        MaybeFunctionKeyStep {
  public default Step<T, R> named(String name) {
    return new NamedStep<>(name, this);
  }

  record NamedStep<T, R>(String name, Step<T, R> step) implements Step<T, R> {}

  public static <T, R> Step<T, R> step(Class<? extends Function<T, Try<R>>> clazz) {
    return new FunctionKeyStep<>(Key.get(clazz));
  }

  record FunctionKeyStep<T, R>(Key<? extends Function<T, Try<R>>> key) implements Step<T, R> {}

  public static <T> Step<Tuple0, T> constStep(T value) {
    var sanitizedValue = requireNonNull(value);
    return step((Tuple0 t) -> success(sanitizedValue));
  }

  public static <T, R> Step<T, R> step(Function<T, Try<R>> function) {
    return new FunctionStep<>(function);
  }

  record FunctionStep<T, R>(Function<T, Try<R>> function) implements Step<T, R> {}

  public static <T, R> Step<T, R> stepFactory(StepFactory<T, R> stepFactory) {
    return new FactoryStep<>(stepFactory);
  }

  record FactoryStep<T, R>(StepFactory<T, R> stepFactory) implements Step<T, R> {}

  public static <T, R> Step<T, R> maybeStep(Class<? extends MaybeFunction<T, R>> clazz) {
    return new MaybeFunctionKeyStep<>(Key.get(clazz));
  }

  record MaybeFunctionKeyStep<T, R>(Key<? extends MaybeFunction<T, R>> key) implements Step<T, R> {}

  public default <S> Step<T, Tuple2<R, S>> append(S value) {
    return new ComposedStep<>(this, step((R r) -> success(tuple(r, value))));
  }

  public default <S> Step<T, S> then(Step<? super R, S> nextStep) {
    return new ComposedStep<>(this, nextStep);
  }

  record ComposedStep<T, S, R>(Step<T, S> first, Step<? super S, R> second) implements Step<T, R> {}
}
