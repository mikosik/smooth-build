package org.smoothbuild.vm.execute;

import org.smoothbuild.compile.lang.base.LabeledLoc;
import org.smoothbuild.compile.lang.base.Loc;

public record TaskInfo(TaskKind kind, String label, Loc loc) implements LabeledLoc {
  public static final int NAME_LENGTH_LIMIT = 43;

  public TaskInfo(TaskKind kind, LabeledLoc label) {
    this(kind, label.label(), label.loc());
  }
}
