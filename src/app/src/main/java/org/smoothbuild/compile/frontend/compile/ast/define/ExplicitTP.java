package org.smoothbuild.compile.frontend.compile.ast.define;

import org.smoothbuild.compile.frontend.lang.base.location.Location;

public sealed class ExplicitTP extends TypeP permits ArrayTP, FuncTP {
  public ExplicitTP(String name, Location location) {
    super(name, location);
  }
}
