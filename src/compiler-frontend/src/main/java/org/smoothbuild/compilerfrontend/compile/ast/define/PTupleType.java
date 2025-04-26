package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.location.Location;

public final class PTupleType extends PExplicitType {
  private final List<PExplicitType> elementTypes;

  public PTupleType(List<PExplicitType> elementTypes, Location location) {
    super("{" + elementTypes.map(PExplicitType::nameText).toString(",") + "}", location);
    this.elementTypes = elementTypes;
  }

  public List<PExplicitType> elementTypes() {
    return elementTypes;
  }
}
