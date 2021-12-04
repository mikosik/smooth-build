package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.util.collect.CountersMap;

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

  @Override
  public void countVars(CountersMap<String> countersMap) {
    countFuncVars(countersMap, resT, paramTs);
  }

  public static void countFuncVars(CountersMap<String> countersMap, TypeN resT,
      ImmutableList<TypeN> paramTs) {
    resT.countVars(countersMap);
    paramTs.forEach(p -> p.countVars(countersMap));
  }

  public TypeN resT() {
    return resT;
  }

  public ImmutableList<TypeN> paramTs() {
    return paramTs;
  }
}
