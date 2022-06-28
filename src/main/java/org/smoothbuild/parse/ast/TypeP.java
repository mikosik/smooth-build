package org.smoothbuild.parse.ast;

import static org.smoothbuild.lang.type.TNamesS.isVarName;

import org.smoothbuild.lang.base.Loc;

public sealed class TypeP extends MonoNamedP permits ArrayTP, FuncTP {
  public TypeP(String name, Loc loc) {
    super(name, loc);
  }

  public boolean isPolytype() {
    return isVarName(name());
  }
}
