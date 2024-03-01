package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.TupleTS;

public record CombineS(TupleTS evaluationT, List<ExprS> elems, Location location) implements ExprS {
  @Override
  public String toString() {
    var elemsString = elems.toString("\n");
    var fields = list(
            "evaluationT = " + evaluationT,
            "elems = [\n" + indent(elemsString) + "\n]",
            "location = " + location)
        .toString("\n");
    return "CombineS(\n" + indent(fields) + "\n)";
  }
}