package org.smoothbuild.common.schedule;

import org.smoothbuild.common.collect.List;

public interface TaskX<R, A> {
  public Output<R> execute(List<A> arg1);
}
