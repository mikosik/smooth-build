package org.smoothbuild.vm.execute;

import org.smoothbuild.compile.lang.base.LabeledLoc;
import org.smoothbuild.compile.lang.base.LabeledLocImpl;
import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.WithLoc;

public record TaskInfo(TaskKind kind, String label, Loc loc) implements WithLoc {
  public static final int NAME_LENGTH_LIMIT = 43;

  public TaskInfo(TaskKind kind, LabeledLoc description) {
    this(kind, description.label(), description.loc());
  }

  public LabeledLoc asExprInfo() {
    return new LabeledLocImpl(label, loc);
  }
}
