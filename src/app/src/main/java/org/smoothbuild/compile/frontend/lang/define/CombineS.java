package org.smoothbuild.compile.frontend.lang.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.Iterables.joinToString;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compile.frontend.lang.base.location.Location;
import org.smoothbuild.compile.frontend.lang.type.TupleTS;

public record CombineS(TupleTS evaluationT, List<ExprS> elems, Location location) implements ExprS {
  @Override
  public String toString() {
    var fields = joinToString(
        "\n",
        "evaluationT = " + evaluationT,
        "elems = [\n" + indent(joinToString(elems, "\n")) + "\n]",
        "location = " + location);
    return "CombineS(\n" + indent(fields) + "\n)";
  }
}
