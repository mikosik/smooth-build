package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.STupleType;
import org.smoothbuild.compilerfrontend.lang.type.SVarSet;

public record SCombine(STupleType evaluationType, List<SExpr> elements, Location location)
    implements SExpr {
  @Override
  public String toSourceCode(SVarSet localVars) {
    return "{" + elements.map(e -> e.toSourceCode(localVars)).toString(", ") + "}";
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SCombine")
        .addField("evaluationType", evaluationType)
        .addListField("elements", elements)
        .addField("location", location)
        .toString();
  }
}
