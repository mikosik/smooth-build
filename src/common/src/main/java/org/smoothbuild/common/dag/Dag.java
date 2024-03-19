package org.smoothbuild.common.dag;

import com.google.inject.Key;
import org.smoothbuild.common.log.base.Label;

/**
 * Directed Acyclic Graph containing operations to execute.
 * @param <R> type of result calculated by this graph.
 */
public sealed interface Dag<R>
    permits Application0,
        Application1,
        Application2,
        Chain,
        Evaluation,
        Injection,
        MaybeApplication,
        Prefix,
        Value {

  public static <R> Dag<R> apply0(Class<? extends TryFunction0<R>> clazz) {
    return apply0(inject(clazz));
  }

  public static <R> Dag<R> apply0(TryFunction0<R> function) {
    return apply0(value(function));
  }

  public static <R> Dag<R> apply0(Dag<? extends TryFunction0<R>> function) {
    return new Application0<>(function);
  }

  public static <A, R> Dag<R> apply1(Class<? extends TryFunction1<A, R>> clazz, Dag<A> argument1) {
    return apply1(inject(clazz), argument1);
  }

  public static <A, R> Dag<R> apply1(TryFunction1<A, R> function, Dag<A> argument1) {
    return apply1(value(function), argument1);
  }

  public static <A, R> Dag<R> apply1(Dag<? extends TryFunction1<A, R>> function, Dag<A> argument1) {
    return new Application1<>(function, argument1);
  }

  public static <A, B, R> Dag<R> apply2(
      Class<? extends TryFunction2<A, B, R>> clazz, Dag<A> argument1, Dag<B> argument2) {
    return apply2(inject(clazz), argument1, argument2);
  }

  public static <A, B, R> Dag<R> apply2(
      TryFunction2<A, B, R> function, Dag<A> argument1, Dag<B> argument2) {
    return apply2(value(function), argument1, argument2);
  }

  public static <A, B, R> Dag<R> apply2(
      Dag<? extends TryFunction2<A, B, R>> function, Dag<A> argument1, Dag<B> argument2) {
    return new Application2<>(function, argument1, argument2);
  }

  public static <A, R> Dag<R> applyMaybeFunction(
      Class<? extends MaybeFunction<A, R>> clazz, Dag<A> argument1) {
    Dag<? extends MaybeFunction<A, R>> maybeFunction = inject(clazz);
    return applyMaybeFunction(maybeFunction, argument1);
  }

  public static <A, R> Dag<R> applyMaybeFunction(
      Dag<? extends MaybeFunction<A, R>> maybeFunction, Dag<A> argument1) {
    return new MaybeApplication<>(maybeFunction, argument1);
  }

  /**
   * Creates Dag node that executes two subnodes in order and returns result of the second one.
   * Useful when evaluation of second node depends on side-effect of evaluation of first node.
   */
  public static <R> Dag<R> chain(Dag<?> first, Dag<R> second) {
    return new Chain<>(first, second);
  }

  /**
   * Creates a DAG that evaluates DAG passed as argument (which produces another DAG) and then
   * evaluates that produced DAG. Useful when you have an inner-DAG that evaluates to some value
   * that is taken as argument by two different nodes in outer-DAG. If such sub-DAG was used as
   * child in both places then it would be evaluated twice. Instead, we can create a node that
   * takes inner-DAG produces outer-DAG. Evaluation of such node has access to evaluation result
   * of inner-DAG and use such value directly when creating outer-DAG - this way evaluation of
   * sub-DAG happens only once.
   */
  public static <R> Dag<R> evaluate(Dag<Dag<R>> dag) {
    return new Evaluation<>(dag);
  }

  public static <R> Dag<R> inject(Class<? extends R> clazz) {
    return inject(Key.get(clazz));
  }

  public static <R> Dag<R> inject(Key<? extends R> key) {
    return new Injection<>(key);
  }

  public static <R> Dag<R> prefix(Label label, Dag<R> dag) {
    return new Prefix<>(label, dag);
  }

  public static <R> Dag<R> value(R value) {
    return new Value<>(value);
  }
}

record Application0<R>(Dag<? extends TryFunction0<R>> function) implements Dag<R> {}

record Application1<A, R>(Dag<? extends TryFunction1<A, R>> function, Dag<A> argument)
    implements Dag<R> {}

record Application2<A, B, R>(
    Dag<? extends TryFunction2<A, B, R>> function, Dag<A> argument1, Dag<B> argument2)
    implements Dag<R> {}

record Chain<R>(Dag<?> first, Dag<R> second) implements Dag<R> {}

record Evaluation<R>(Dag<Dag<R>> dag) implements Dag<R> {}

record Injection<R>(Key<? extends R> key) implements Dag<R> {}

record MaybeApplication<A, R>(Dag<? extends MaybeFunction<A, R>> function, Dag<A> argument)
    implements Dag<R> {}

record Prefix<R>(Label label, Dag<R> dag) implements Dag<R> {}

record Value<R>(R value) implements Dag<R> {}
