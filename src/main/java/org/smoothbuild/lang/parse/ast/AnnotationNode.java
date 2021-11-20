package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Location;

public final class AnnotationNode extends ExprNode {
  private final StringNode path;
  private final boolean isPure;

  public AnnotationNode(StringNode path, boolean isPure, Location location) {
    super(location);
    this.path = path;
    this.isPure = isPure;
  }

  public StringNode path() {
    return path;
  }

  public boolean isPure() {
    return isPure;
  }
}
