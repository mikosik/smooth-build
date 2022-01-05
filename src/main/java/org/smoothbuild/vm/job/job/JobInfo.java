package org.smoothbuild.vm.job.job;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.define.Nal;

public record JobInfo(JobKind kind, String name, Loc loc) implements Nal {
  public static final int NAME_LENGTH_LIMIT = 43;

  public JobInfo(JobKind kind, Nal nal) {
    this(kind, nal.name(), nal.loc());
  }
}
