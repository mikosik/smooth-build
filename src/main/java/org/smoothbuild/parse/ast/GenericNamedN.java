package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.WithLoc;

public sealed abstract class GenericNamedN extends WithLoc implements AstNode, NamedN
    permits GenericRefableN {
  private final String name;

  public GenericNamedN(String name, Loc loc) {
    super(loc);
    this.name = name;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof GenericNamedN that
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
