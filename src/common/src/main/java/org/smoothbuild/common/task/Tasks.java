package org.smoothbuild.common.task;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.concurrent.Promise.promise;
import static org.smoothbuild.common.task.Output.output;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.base.Label;

public class Tasks {
  public static <V> Promise<Maybe<V>> argument(V value) {
    return promise(some(value));
  }

  public static <A1, R> Task1<A1, R> task1(Label label, Function<A1, R> function) {
    return arg1 -> output(function.apply(arg1), label, list());
  }

  public static <A1, A2, R> Task2<A1, A2, R> task2(Label label, BiFunction<A1, A2, R> function) {
    return (arg1, arg2) -> output(function.apply(arg1, arg2), label, list());
  }

  public static <A, R> TaskX<A, R> taskX(Label label, Function<List<A>, R> function) {
    return (arg) -> output(function.apply(arg), label, list());
  }
}
