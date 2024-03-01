package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.base.location.Location;

public final class ImplicitTP extends TypeP {
  public ImplicitTP(Location location) {
    super("?", location);
  }
}
