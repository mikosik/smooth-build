package org.smoothbuild.common.schedule;

public interface Task2<A1, A2, R> {
  public Output<R> execute(A1 arg1, A2 arg2);
}
