package org.smoothbuild.common.plan;

import com.google.inject.Key;

/**
 * Plan of operations to operations to execute.
 * @param <R> type of result calculated by this graph.
 */
public sealed interface Plan<R>
    permits Application0,
        Application1,
        Application2,
        Chain,
        Evaluation,
        Injection,
        MaybeApplication,
        Value {

  public static <R> Plan<R> apply0(Class<? extends TryFunction0<R>> clazz) {
    return apply0(inject(clazz));
  }

  public static <R> Plan<R> apply0(TryFunction0<R> function) {
    return apply0(value(function));
  }

  public static <R> Plan<R> apply0(Plan<? extends TryFunction0<R>> function) {
    return new Application0<>(function);
  }

  public static <A, R> Plan<R> apply1(
      Class<? extends TryFunction1<A, R>> clazz, Plan<A> argument1) {
    return apply1(inject(clazz), argument1);
  }

  public static <A, R> Plan<R> apply1(TryFunction1<A, R> function, Plan<A> argument1) {
    return apply1(value(function), argument1);
  }

  public static <A, R> Plan<R> apply1(
      Plan<? extends TryFunction1<A, R>> function, Plan<A> argument1) {
    return new Application1<>(function, argument1);
  }

  public static <A, B, R> Plan<R> apply2(
      Class<? extends TryFunction2<A, B, R>> clazz, Plan<A> argument1, Plan<B> argument2) {
    return apply2(inject(clazz), argument1, argument2);
  }

  public static <A, B, R> Plan<R> apply2(
      TryFunction2<A, B, R> function, Plan<A> argument1, Plan<B> argument2) {
    return apply2(value(function), argument1, argument2);
  }

  public static <A, B, R> Plan<R> apply2(
      Plan<? extends TryFunction2<A, B, R>> function, Plan<A> argument1, Plan<B> argument2) {
    return new Application2<>(function, argument1, argument2);
  }

  public static <A, R> Plan<R> applyMaybeFunction(
      Class<? extends MaybeFunction<A, R>> clazz, Plan<A> argument1) {
    Plan<? extends MaybeFunction<A, R>> maybeFunction = inject(clazz);
    return applyMaybeFunction(maybeFunction, argument1);
  }

  public static <A, R> Plan<R> applyMaybeFunction(
      Plan<? extends MaybeFunction<A, R>> maybeFunction, Plan<A> argument1) {
    return new MaybeApplication<>(maybeFunction, argument1);
  }

  /**
   * Creates Plan that executes two plans in order and returns result of the second one.
   * Useful when evaluation of second depends on side-effect of evaluation of first one.
   */
  public static <R> Plan<R> chain(Plan<?> first, Plan<R> second) {
    return new Chain<>(first, second);
  }

  /**
   * Creates a Plan that evaluates Plan passed as argument (which produces another Plan) and then
   * evaluates that produced Plan. Useful when you have an inner-Plan that evaluates to some value
   * that is taken as argument by two different Plans in outer-Plan. If such sub-Plan was used as
   * child in both places then it would be evaluated twice. Instead, we can create a node that
   * takes inner-Plan produces outer-Plan. Evaluation of such node has access to evaluation result
   * of inner-Plan and use such value directly when creating outer-Plan - this way evaluation of
   * sub-Plan happens only once.
   */
  public static <R> Plan<R> evaluate(Plan<Plan<R>> plan) {
    return new Evaluation<>(plan);
  }

  public static <R> Plan<R> inject(Class<? extends R> clazz) {
    return inject(Key.get(clazz));
  }

  public static <R> Plan<R> inject(Key<? extends R> key) {
    return new Injection<>(key);
  }

  public static <R> Plan<R> value(R value) {
    return new Value<>(value);
  }
}

record Application0<R>(Plan<? extends TryFunction0<R>> function) implements Plan<R> {}

record Application1<A, R>(Plan<? extends TryFunction1<A, R>> function, Plan<A> argument)
    implements Plan<R> {}

record Application2<A, B, R>(
    Plan<? extends TryFunction2<A, B, R>> function, Plan<A> argument1, Plan<B> argument2)
    implements Plan<R> {}

record Chain<R>(Plan<?> first, Plan<R> second) implements Plan<R> {}

record Evaluation<R>(Plan<Plan<R>> plan) implements Plan<R> {}

record Injection<R>(Key<? extends R> key) implements Plan<R> {}

record MaybeApplication<A, R>(Plan<? extends MaybeFunction<A, R>> function, Plan<A> argument)
    implements Plan<R> {}

record Value<R>(R value) implements Plan<R> {}
