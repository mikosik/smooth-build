package org.smoothbuild.vm.job;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.Nal;

public record TaskInfo(TaskKind kind, String name, Loc loc) implements Nal {
  public static final int NAME_LENGTH_LIMIT = 43;

  public TaskInfo(TaskKind kind, Nal nal) {
    this(kind, nal.name(), nal.loc());
  }
}
