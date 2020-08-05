package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.Location;

public class RefNode extends ExprNode {
  private final String name;
  private NamedNode target;

  public RefNode(String name, Location location) {
    super(location);
    this.name = name;
  }

  public String name() {
    return name;
  }

  public void setTarget(NamedNode named) {
    this.target = named;
  }

  public NamedNode target() {
    return target;
  }
}
