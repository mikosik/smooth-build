package org.smoothbuild.compile.ps.ast.expr;

import static org.smoothbuild.util.Strings.q;

import org.smoothbuild.compile.lang.base.Loc;

public final class RefP extends MonoizableP {
  private final String name;

  public RefP(String name, Loc loc) {
    super(loc);
    this.name = name;
  }

  public String name() {
    return name;
  }

  @Override
  public String toString() {
    return "RefP(`" + q(name) + "`)";
  }
}
