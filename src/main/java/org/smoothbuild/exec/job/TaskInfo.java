package org.smoothbuild.exec.job;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.Nal;

public record TaskInfo(TaskKind kind, String name, Location location) implements Nal {
  public static final int NAME_LENGTH_LIMIT = 40;

  public TaskInfo(TaskKind kind, Nal nal) {
    this(kind, nal.name(), nal.location());
  }
}
