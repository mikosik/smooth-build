package org.smoothbuild.lang.expr;

import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;
import static org.smoothbuild.vm.job.TaskInfo.NAME_LENGTH_LIMIT;

import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.type.impl.StringTS;

public record StringS(StringTS type, String string, Loc loc) implements ExprS {
  @Override
  public String name() {
    return escapedAndLimitedWithEllipsis(string, NAME_LENGTH_LIMIT);
  }
}
