package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.log.location.Location;

public final class PArrayType extends PExplicitType {
  private final PExplicitType elementType;

  public PArrayType(PExplicitType elementType, Location location) {
    super("[" + elementType.nameText() + "]", location);
    this.elementType = elementType;
  }

  public PExplicitType elemT() {
    return elementType;
  }
}
