package org.smoothbuild.common.function;

import java.util.concurrent.ConcurrentHashMap;

class MemoizingFunction1<A, R, T extends Throwable> implements Function1<A, R, T> {
  private final ConcurrentHashMap<A, Function0<R, T>> map;
  private final Function1<A, R, T> mapper;

  public MemoizingFunction1(Function1<A, R, T> function1) {
    this.map = new ConcurrentHashMap<>();
    this.mapper = function1;
  }

  @Override
  public R apply(A argument) throws T {
    var memoizer = map.computeIfAbsent(argument, a -> Function0.memoizer(() -> mapper.apply(a)));
    return memoizer.apply();
  }
}
