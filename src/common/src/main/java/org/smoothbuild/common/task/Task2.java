package org.smoothbuild.common.task;

public interface Task2<A1, A2, R> {
  public Output<R> execute(A1 arg1, A2 arg2);
}
