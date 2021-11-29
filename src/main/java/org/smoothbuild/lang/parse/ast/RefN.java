package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.like.EvalLike;

public final class RefN extends ExprN {
  private final String name;
  private EvalLike referenced;

  public RefN(String name, Location location) {
    super(location);
    this.name = name;
  }

  public String name() {
    return name;
  }

  public void setReferenced(EvalLike referenced) {
    this.referenced = referenced;
  }

  public EvalLike referenced() {
    return referenced;
  }

  @Override
  public String toString() {
    return RefN.class.getName() + "(" + name + ")";
  }
}
