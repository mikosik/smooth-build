package org.smoothbuild.task.base;

import org.smoothbuild.lang.base.SValue;

public interface Result<T extends SValue> {
  public T value();
}
