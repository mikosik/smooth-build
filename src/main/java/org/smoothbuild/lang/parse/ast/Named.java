package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.Location;

public interface Named {
  public String name();

  public Location location();
}
