package org.smoothbuild.compile.fs.ps.ast.define;

import org.smoothbuild.compile.fs.lang.base.location.Location;

public sealed class ExplicitTP extends TypeP permits ArrayTP, FuncTP {
  public ExplicitTP(String name, Location location) {
    super(name, location);
  }
}
