package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.util.collect.CountersMap;

import com.google.common.collect.ImmutableList;

public final class FuncTN extends TypeN {
  private final TypeN resT;
  private final ImmutableList<TypeN> paramsT;

  public FuncTN(TypeN resT, ImmutableList<TypeN> paramsT, Loc loc) {
    super("[" + resT.name() + "]", loc);
    this.resT = resT;
    this.paramsT = paramsT;
  }

  @Override
  public boolean isPolytype() {
    return resT.isPolytype() || paramsT.stream().anyMatch(TypeN::isPolytype);
  }

  @Override
  public void countVars(CountersMap<String> countersMap) {
    countFuncVars(countersMap, resT, paramsT);
  }

  public static void countFuncVars(CountersMap<String> countersMap, TypeN resultType,
      ImmutableList<TypeN> paramTypes) {
    resultType.countVars(countersMap);
    paramTypes.forEach(p -> p.countVars(countersMap));
  }

  public TypeN resType() {
    return resT;
  }

  public ImmutableList<TypeN> paramTypes() {
    return paramsT;
  }
}
