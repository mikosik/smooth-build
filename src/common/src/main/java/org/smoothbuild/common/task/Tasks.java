package org.smoothbuild.common.task;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.concurrent.Promise.promise;
import static org.smoothbuild.common.task.Output.output;

import java.util.function.Function;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.base.Label;

public class Tasks {
  public static <V> Promise<Maybe<V>> argument(V value) {
    return promise(some(value));
  }

  public static <R, A> Task1<R, A> task1(Label label, Function<A, R> mapper) {
    return argument -> output(mapper.apply(argument), label, list());
  }
}
