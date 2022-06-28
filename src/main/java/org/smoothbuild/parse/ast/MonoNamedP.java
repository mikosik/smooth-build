package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Loc;

public sealed class MonoNamedP extends MonoP implements NamedP
    permits AnnP, MonoRefableP, StructP, TypeP {
  private final String name;

  public MonoNamedP(String name, Loc loc) {
    super(loc);
    this.name = name;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof MonoNamedP that
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
