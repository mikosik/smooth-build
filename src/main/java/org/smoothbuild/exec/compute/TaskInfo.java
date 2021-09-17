package org.smoothbuild.exec.compute;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.Named;

public record TaskInfo(TaskKind kind, String name, Location location) implements Named {
  public static final int NAME_LENGTH_LIMIT = 40;
}
