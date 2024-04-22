package org.smoothbuild.common.schedule;

import org.smoothbuild.common.concurrent.Promise;

public interface Evaluation<T> {
  public Promise<T> result();
}
