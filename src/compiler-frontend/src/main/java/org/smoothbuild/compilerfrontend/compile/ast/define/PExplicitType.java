package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.log.location.Location;

public abstract sealed class PExplicitType extends PType
    permits PArrayType, PFuncType, PTypeReference {
  protected PExplicitType(String nameText, Location location) {
    super(nameText, location);
  }
}
