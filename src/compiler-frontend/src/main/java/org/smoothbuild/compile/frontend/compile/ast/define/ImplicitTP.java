package org.smoothbuild.compile.frontend.compile.ast.define;

import org.smoothbuild.compile.frontend.lang.base.location.Location;

public final class ImplicitTP extends TypeP {
  public ImplicitTP(Location location) {
    super("?", location);
  }
}
