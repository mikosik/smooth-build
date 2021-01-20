package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Location;

public class RefNode extends ExprNode {
  private final String name;
  private RefTarget target;

  public RefNode(String name, Location location) {
    super(location);
    this.name = name;
  }

  public String name() {
    return name;
  }

  public void setTarget(RefTarget target) {
    this.target = target;
  }

  public RefTarget target() {
    return target;
  }
}
