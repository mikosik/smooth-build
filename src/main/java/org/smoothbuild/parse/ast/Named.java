package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.message.Location;

public interface Named {
  public String name();

  public Location location();
}
