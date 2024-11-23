package org.smoothbuild.common.schedule;

public interface Task1<A1, R> {
  public Output<R> execute(A1 arg1);
}
