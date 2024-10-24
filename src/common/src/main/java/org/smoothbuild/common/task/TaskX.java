package org.smoothbuild.common.task;

import org.smoothbuild.common.collect.List;

public interface TaskX<A, R> {
  public Output<R> execute(List<A> arg1);
}
