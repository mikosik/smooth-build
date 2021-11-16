package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.like.EvaluableLike;

public class RefNode extends ExprNode {
  private final String name;
  private EvaluableLike referenced;

  public RefNode(String name, Location location) {
    super(location);
    this.name = name;
  }

  public String name() {
    return name;
  }

  public void setReferenced(EvaluableLike referenced) {
    this.referenced = referenced;
  }

  public EvaluableLike referenced() {
    return referenced;
  }

  @Override
  public String toString() {
    return RefNode.class.getName() + "(" + name + ")";
  }
}
