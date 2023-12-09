package org.smoothbuild.common.tuple;

import org.smoothbuild.common.function.Function1;

public record Tuple2<E1, E2>(E1 e1, E2 e2) {
  public <R, T extends Throwable> Tuple2<R, E2> map1(Function1<E1, R, T> mapper) throws T {
    return new Tuple2<>(mapper.apply(e1), e2);
  }

  public <R, T extends Throwable> Tuple2<E1, R> map2(Function1<E2, R, T> mapper) throws T {
    return new Tuple2<>(e1, mapper.apply(e2));
  }
}
