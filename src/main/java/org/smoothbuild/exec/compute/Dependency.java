package org.smoothbuild.exec.compute;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;

public interface Dependency {
  Task task();

  Type type();

  Location location();
}
