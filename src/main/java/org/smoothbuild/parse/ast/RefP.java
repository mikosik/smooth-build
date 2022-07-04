package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.util.Strings;

public final class RefP extends GenericP implements ExprP {
  private final String name;
  private MonoTS inferredMonoT;

  public RefP(String name, Loc loc) {
    super(loc);
    this.name = name;
  }

  public String name() {
    return name;
  }

  public String q() {
    return Strings.q(name);
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
