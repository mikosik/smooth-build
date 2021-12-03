package org.smoothbuild.lang.expr;

import static org.smoothbuild.exec.job.TaskInfo.NAME_LENGTH_LIMIT;
import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.type.impl.StringTS;

public record StringS(StringTS type, String string, Loc loc) implements ExprS {
  @Override
  public String name() {
    return escapedAndLimitedWithEllipsis(string, NAME_LENGTH_LIMIT);
  }
}
