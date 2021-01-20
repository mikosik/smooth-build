package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.like.ReferencableLike;

public class RefNode extends ExprNode {
  private final String name;
  private ReferencableLike target;

  public RefNode(String name, Location location) {
    super(location);
    this.name = name;
  }

  public String name() {
    return name;
  }

  public void setTarget(ReferencableLike target) {
    this.target = target;
  }

  public ReferencableLike target() {
    return target;
  }
}
