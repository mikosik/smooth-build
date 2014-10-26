package org.smoothbuild.task.save;

import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.function.base.Name;

public interface Saver<T extends Value> {
  public void save(Name name, T value);
}
