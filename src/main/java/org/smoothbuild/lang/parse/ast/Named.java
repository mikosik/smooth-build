package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Location;

public interface Named {
  public String name();

  public Location location();
}
