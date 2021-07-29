package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Location;

public class NativeNode extends ExprNode {
  private final String path;
  private final boolean isPure;

  public NativeNode(String path, boolean isPure, Location location) {
    super(location);
    this.path = path;
    this.isPure = isPure;
  }

  public String path() {
    return path;
  }

  public boolean isPure() {
    return isPure;
  }
}
