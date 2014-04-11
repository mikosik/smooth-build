package org.smoothbuild.task.base;

import org.smoothbuild.lang.type.SValue;

public interface Result<T extends SValue> {
  public T value();
}
