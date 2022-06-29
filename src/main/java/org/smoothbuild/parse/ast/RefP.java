package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.like.Refable;
import org.smoothbuild.lang.type.MonoTS;

public final class RefP extends GenericP implements ExprP {
  private final String name;
  private Refable referenced;
  private MonoTS inferredMonoT;

  public RefP(String name, Loc loc) {
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
    return "RefP(`" + name + "`)";
  }

  public void setInferredMonoType(MonoTS inferredMonoT) {
    this.inferredMonoT = inferredMonoT;
  }

  public MonoTS inferredMonoT() {
    return inferredMonoT;
  }
}
