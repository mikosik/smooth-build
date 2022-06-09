package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Loc;

public sealed class PolyNamedN extends PolyAstNode implements NamedN
    permits PolyRefableN {
  private final String name;

  public PolyNamedN(String name, Loc loc) {
    super(loc);
    this.name = name;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof PolyNamedN that
        && this.name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public String name() {
    return name;
  }
}
