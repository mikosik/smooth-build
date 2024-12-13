package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.log.location.Location;

public final class PArrayType extends PExplicitType {
  private final PType elemT;

  public PArrayType(PType elemT, Location location) {
    super("[" + elemT.nameText() + "]", location);
    this.elemT = elemT;
  }

  public PType elemT() {
    return elemT;
  }
}
