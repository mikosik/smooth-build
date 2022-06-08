package org.smoothbuild.parse.ast;

import static org.smoothbuild.lang.type.TNamesS.isVarName;

import org.smoothbuild.lang.base.Loc;

public sealed class TypeN extends MonoNamedN permits ArrayTN, FuncTN {
  public TypeN(String name, Loc loc) {
    super(name, loc);
  }

  public boolean isPolytype() {
    return isVarName(name());
  }
}
