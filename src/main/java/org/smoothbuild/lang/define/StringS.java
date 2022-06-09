package org.smoothbuild.lang.define;

import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;
import static org.smoothbuild.vm.job.TaskInfo.NAME_LENGTH_LIMIT;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.StringTS;

public record StringS(StringTS type, String string, Loc loc) implements CnstS {
  @Override
  public String name() {
    return escapedAndLimitedWithEllipsis(string, NAME_LENGTH_LIMIT);
  }
}
