package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.base.location.Location;

public sealed class PExplicitType extends PType permits PArrayType, PFuncType {
  public PExplicitType(String name, Location location) {
    super(name, location);
  }
}