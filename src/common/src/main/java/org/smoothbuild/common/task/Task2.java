package org.smoothbuild.common.task;

public interface Task2<R, A1, A2> {
  public Output<R> execute(A1 arg1, A2 arg2);
}
