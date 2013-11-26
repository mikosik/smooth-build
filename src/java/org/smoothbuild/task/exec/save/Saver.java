package org.smoothbuild.task.exec.save;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.type.SValue;

public interface Saver<T extends SValue> {
  public void save(Name name, T value);
}
