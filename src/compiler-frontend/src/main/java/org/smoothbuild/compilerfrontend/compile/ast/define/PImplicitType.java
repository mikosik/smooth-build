package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.log.location.Location;

public final class PImplicitType extends PType {
  public PImplicitType(Location location) {
    super("?", location);
  }
}
