package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.ArrayTS;

public record OrderS(ArrayTS evaluationType, List<ExprS> elems, Location location)
    implements ExprS {
  @Override
  public String toString() {
    var fields = list(
            "evaluationType = " + evaluationType,
            "elems = [\n" + indent(elems.toString("\n")) + "\n]",
            "location = " + location)
        .toString("\n");
    return "OrderS(\n" + indent(fields) + "\n)";
  }
}
