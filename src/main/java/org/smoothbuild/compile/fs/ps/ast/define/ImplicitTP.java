package org.smoothbuild.compile.fs.ps.ast.define;

import org.smoothbuild.compile.fs.lang.base.location.Location;

public final class ImplicitTP extends TypeP {
  public ImplicitTP(Location location) {
    super("?", location);
  }
}
