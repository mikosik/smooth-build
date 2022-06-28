package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Loc;

public final class ArrayTP extends TypeP {
  private final TypeP elemT;

  public ArrayTP(TypeP elemT, Loc loc) {
    super("[" + elemT.name() + "]", loc);
    this.elemT = elemT;
  }

  @Override
  public boolean isPolytype() {
    return elemT.isPolytype();
  }

  public TypeP elemT() {
    return elemT;
  }
}
