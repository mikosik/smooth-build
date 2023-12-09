package org.smoothbuild.compile.frontend.lang.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compile.frontend.lang.base.location.Location;
import org.smoothbuild.compile.frontend.lang.type.ArrayTS;

public record OrderS(ArrayTS evaluationT, List<ExprS> elems, Location location) implements ExprS {
  @Override
  public String toString() {
    var fields = list(
            "evaluationT = " + evaluationT,
            "elems = [\n" + indent(elems.toString("\n")) + "\n]",
            "location = " + location)
        .toString("\n");
    return "OrderS(\n" + indent(fields) + "\n)";
  }
}
