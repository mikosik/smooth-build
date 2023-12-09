package org.smoothbuild.common.tuple;

public class Tuples {
  public static Tuple0 tuple() {
    return new Tuple0();
  }

  public static <E1> Tuple1<E1> tuple(E1 e1) {
    return new Tuple1<>(e1);
  }

  public static <E1, E2> Tuple2<E1, E2> tuple(E1 e1, E2 e2) {
    return new Tuple2<>(e1, e2);
  }
}
