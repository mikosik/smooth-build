package org.smoothbuild.task.save;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.value.Value;

public interface Saver<T extends Value> {
  public void save(Name name, T value);
}
