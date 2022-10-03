package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;
import static org.smoothbuild.vm.execute.TaskInfo.NAME_LENGTH_LIMIT;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.StringTS;

public record StringS(StringTS type, String string, Loc loc) implements InstS {
  @Override
  public String label() {
    return escapedAndLimitedWithEllipsis(string, NAME_LENGTH_LIMIT);
  }
}
