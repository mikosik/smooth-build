package org.smoothbuild.common.schedule;

public interface Task1<R, A1> {
  public Output<R> execute(A1 arg1);
}
