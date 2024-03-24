package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.base.location.Location;

public final class PImplicitType extends PType {
  public PImplicitType(Location location) {
    super("?", location);
  }
}
