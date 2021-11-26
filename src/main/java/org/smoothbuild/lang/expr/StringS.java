package org.smoothbuild.lang.expr;

import static org.smoothbuild.exec.job.TaskInfo.NAME_LENGTH_LIMIT;
import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.impl.StringTypeS;

public record StringS(StringTypeS type, String string, Location location) implements LiteralS {
  @Override
  public String name() {
    return toShortString();
  }

  @Override
  public String toShortString() {
    return escapedAndLimitedWithEllipsis(string, NAME_LENGTH_LIMIT);
  }
}
