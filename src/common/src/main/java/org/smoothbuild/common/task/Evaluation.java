package org.smoothbuild.common.task;

import org.smoothbuild.common.concurrent.Promise;

public interface Evaluation<T> {
  public Promise<T> result();
}
