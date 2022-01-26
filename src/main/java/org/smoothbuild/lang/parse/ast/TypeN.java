package org.smoothbuild.lang.parse.ast;

import static org.smoothbuild.lang.base.type.api.TypeNames.isVarName;

import org.smoothbuild.lang.base.define.Loc;

public sealed class TypeN extends NamedN permits ArrayTN, FuncTN {
  public TypeN(String name, Loc loc) {
    super(name, loc);
  }

  public boolean isPolytype() {
    return isVarName(name());
  }
}
