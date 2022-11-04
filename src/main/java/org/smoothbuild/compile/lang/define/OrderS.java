package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Lists.joinToString;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.ArrayTS;

import com.google.common.collect.ImmutableList;

public record OrderS(ArrayTS evalT, ImmutableList<ExprS> elems, Loc loc) implements OperS {
  @Override
  public String toString() {
    var fields = joinToString("\n",
        "evalT = " + evalT,
        "elems = [\n" + indent(joinToString(elems, "\n")) + "\n]",
        "loc = " + loc
    );
    return "OrderS(\n" + indent(fields) + "\n)";
  }
}
