package org.smoothbuild.compile.ps.ast.type;

import org.smoothbuild.compile.lang.base.location.Location;

import com.google.common.collect.ImmutableList;

public final class FuncTP extends TypeP {
  private final TypeP resT;
  private final ImmutableList<TypeP> paramTs;

  public FuncTP(TypeP resT, ImmutableList<TypeP> paramTs, Location location) {
    super("[" + resT.name() + "]", location);
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
