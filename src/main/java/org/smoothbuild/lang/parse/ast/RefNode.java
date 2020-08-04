package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.Location;

public class RefNode extends ExprNode {
  private final String name;

  public RefNode(String name, Location location) {
    super(location);
    this.name = name;
  }

  public String name() {
    return name;
  }
}
