package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Loc;

public sealed class MonoNamedN extends MonoAstNode implements NamedN
    permits AnnN, ArgN, RefableN, StructN, TypeN {
  private final String name;

  public MonoNamedN(String name, Loc loc) {
    super(loc);
    this.name = name;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof MonoNamedN that
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
