package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Loc;

public final class ArrayTN extends TypeN {
  private final TypeN elemT;

  public ArrayTN(TypeN elemT, Loc loc) {
    super("[" + elemT.name() + "]", loc);
    this.elemT = elemT;
  }

  @Override
  public boolean isPolytype() {
    return elemT.isPolytype();
  }

  public TypeN elemT() {
    return elemT;
  }
}
