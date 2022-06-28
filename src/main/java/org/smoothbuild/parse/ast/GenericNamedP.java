package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.WithLoc;

public sealed abstract class GenericNamedP extends WithLoc implements Parsed, NamedP
    permits GenericRefableP {
  private final String name;

  public GenericNamedP(String name, Loc loc) {
    super(loc);
    this.name = name;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof GenericNamedP that
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
