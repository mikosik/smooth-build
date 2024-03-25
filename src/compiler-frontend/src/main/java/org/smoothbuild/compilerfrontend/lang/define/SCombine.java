package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.STupleType;

public record SCombine(STupleType evaluationType, List<SExpr> elems, Location location)
    implements SExpr {
  @Override
  public String toString() {
    var elemsString = elems.toString("\n");
    var fields = list(
            "evaluationType = " + evaluationType,
            "elems = [\n" + indent(elemsString) + "\n]",
            "location = " + location)
        .toString("\n");
    return "SCombine(\n" + indent(fields) + "\n)";
  }
}
