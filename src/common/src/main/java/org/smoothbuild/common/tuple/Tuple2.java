package org.smoothbuild.common.tuple;

import org.smoothbuild.common.function.Function1;

public record Tuple2<E1, E2>(E1 element1, E2 element2) {
  public <R, T extends Throwable> Tuple2<R, E2> map1(Function1<E1, R, T> mapper) throws T {
    return new Tuple2<>(mapper.apply(element1), element2);
  }

  public <R, T extends Throwable> Tuple2<E1, R> map2(Function1<E2, R, T> mapper) throws T {
    return new Tuple2<>(element1, mapper.apply(element2));
  }
}
