package org.smoothbuild.parse.ast.type;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.NalImpl;

public sealed class TypeP extends NalImpl permits ArrayTP, FuncTP {
  public TypeP(String name, Loc loc) {
    super(name, loc);
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof TypeP that
        && this.name().equals(that.name());
  }

  @Override
  public int hashCode() {
    return name().hashCode();
  }
}
