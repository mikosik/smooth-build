package org.smoothbuild.compile.ps.ast.type;

import org.smoothbuild.compile.lang.base.Loc;

public final class ArrayTP extends TypeP {
  private final TypeP elemT;

  public ArrayTP(TypeP elemT, Loc loc) {
    super("[" + elemT.name() + "]", loc);
    this.elemT = elemT;
  }

  public TypeP elemT() {
    return elemT;
  }
}
