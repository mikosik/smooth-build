package org.smoothbuild.lang.define;

import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;
import static org.smoothbuild.vm.job.TaskInfo.NAME_LENGTH_LIMIT;

import java.util.function.Function;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.StringTS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.VarS;

public record StringS(StringTS type, String string, Loc loc) implements ValS {
  @Override
  public String name() {
    return escapedAndLimitedWithEllipsis(string, NAME_LENGTH_LIMIT);
  }

  @Override
  public ExprS mapVars(Function<VarS, TypeS> mapper) {
    return this;
  }
}
