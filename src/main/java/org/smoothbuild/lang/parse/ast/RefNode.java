package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.like.ReferencableLike;

public class RefNode extends ExprNode {
  private final String name;
  private ReferencableLike referenced;

  public RefNode(String name, Location location) {
    super(location);
    this.name = name;
  }

  public String name() {
    return name;
  }

  public void setReferenced(ReferencableLike referenced) {
    this.referenced = referenced;
  }

  public ReferencableLike referenced() {
    return referenced;
  }

  @Override
  public String toString() {
    return RefNode.class.getName() + "(" + name + ")";
  }
}
