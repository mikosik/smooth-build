package org.smoothbuild.vm.job.job;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.define.Nal;

public record TaskInfo(TaskKind kind, String name, Loc loc) implements Nal {
  public static final int NAME_LENGTH_LIMIT = 40;

  public TaskInfo(TaskKind kind, Nal nal) {
    this(kind, nal.name(), nal.loc());
  }
}
