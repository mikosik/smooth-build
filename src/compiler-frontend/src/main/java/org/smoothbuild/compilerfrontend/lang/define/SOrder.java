package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SArrayType;

public record SOrder(SArrayType evaluationType, List<SExpr> elements, Location location)
    implements SExpr {
  @Override
  public String toString() {
    var fields = list(
            "evaluationType = " + evaluationType,
            "elements = [\n" + indent(elements.toString("\n")) + "\n]",
            "location = " + location)
        .toString("\n");
    return "SOrder(\n" + indent(fields) + "\n)";
  }
}
