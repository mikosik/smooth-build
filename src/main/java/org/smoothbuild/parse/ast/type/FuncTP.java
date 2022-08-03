package org.smoothbuild.parse.ast.type;

import org.smoothbuild.lang.base.Loc;

import com.google.common.collect.ImmutableList;

public final class FuncTP extends TypeP {
  private final TypeP resT;
  private final ImmutableList<TypeP> paramTs;

  public FuncTP(TypeP resT, ImmutableList<TypeP> paramTs, Loc loc) {
    super("[" + resT.name() + "]", loc);
    this.resT = resT;
    this.paramTs = paramTs;
  }

  public TypeP resT() {
    return resT;
  }

  public ImmutableList<TypeP> paramTs() {
    return paramTs;
  }
}
