package org.smoothbuild.task.exec.save;

import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.function.base.Name;

public interface Saver<T extends SValue> {
  public void save(Name name, T value);
}
