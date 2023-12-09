package org.smoothbuild.common.tuple;

public record Tuple2<E1, E2>(E1 e1, E2 e2) {
  public static <E1, E2> Tuple2<E1, E2> tuple2(E1 e1, E2 e2) {
    return new Tuple2<>(e1, e2);
  }
}
