package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.define.Loc;

import com.google.common.collect.ImmutableList;

public final class FuncTN extends TypeN {
  private final TypeN resT;
  private final ImmutableList<TypeN> paramTs;

  public FuncTN(TypeN resT, ImmutableList<TypeN> paramTs, Loc loc) {
    super("[" + resT.name() + "]", loc);
    this.resT = resT;
    this.paramTs = paramTs;
  }

  @Override
  public boolean isPolytype() {
    return resT.isPolytype() || paramTs.stream().anyMatch(TypeN::isPolytype);
  }

  public TypeN resT() {
    return resT;
  }

  public ImmutableList<TypeN> paramTs() {
    return paramTs;
  }
}
