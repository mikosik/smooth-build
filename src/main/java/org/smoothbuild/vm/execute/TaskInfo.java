package org.smoothbuild.vm.execute;

import org.smoothbuild.compile.lang.base.ExprInfo;
import org.smoothbuild.compile.lang.base.ExprInfoImpl;
import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.WithLoc;

public record TaskInfo(TaskKind kind, String label, Loc loc) implements WithLoc {
  public static final int NAME_LENGTH_LIMIT = 43;

  public TaskInfo(TaskKind kind, ExprInfo description) {
    this(kind, description.label(), description.loc());
  }

  public ExprInfo asExprInfo() {
    return new ExprInfoImpl(label, loc);
  }
}
