package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.like.Refable;

public final class RefN extends GenericAstNode implements ExprN {
  private final String name;
  private Refable referenced;

  public RefN(String name, Loc loc) {
    super(loc);
    this.name = name;
  }

  public String name() {
    return name;
  }

  public void setReferenced(Refable referenced) {
    this.referenced = referenced;
  }

  public Refable referenced() {
    return referenced;
  }

  @Override
  public String toString() {
    return "RefN(`" + name + "`)";
  }
}
