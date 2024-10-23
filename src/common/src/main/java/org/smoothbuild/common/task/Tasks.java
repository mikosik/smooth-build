package org.smoothbuild.common.task;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.concurrent.Promise.promise;
import static org.smoothbuild.common.task.Output.output;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.base.Label;

public class Tasks {
  public static <V> Promise<Maybe<V>> argument(V value) {
    return promise(some(value));
  }

  public static <R, A1> Task1<R, A1> task1(Label label, Function<A1, R> function) {
    return arg1 -> output(function.apply(arg1), label, list());
  }

  public static <R, A1, A2> Task2<R, A1, A2> task2(Label label, BiFunction<A1, A2, R> function) {
    return (arg1, arg2) -> output(function.apply(arg1, arg2), label, list());
  }
}
