package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.util.collect.CountersMap;

import com.google.common.collect.ImmutableList;

public final class FuncTypeN extends TypeN {
  private final TypeN resType;
  private final ImmutableList<TypeN> paramTypes;

  public FuncTypeN(
      TypeN resType, ImmutableList<TypeN> paramTypes, Loc loc) {
    super("[" + resType.name() + "]", loc);
    this.resType = resType;
    this.paramTypes = paramTypes;
  }

  @Override
  public boolean isPolytype() {
    return resType.isPolytype() || paramTypes.stream().anyMatch(TypeN::isPolytype);
  }

  @Override
  public void countVars(CountersMap<String> countersMap) {
    countFuncVars(countersMap, resType, paramTypes);
  }

  public static void countFuncVars(CountersMap<String> countersMap, TypeN resultType,
      ImmutableList<TypeN> paramTypes) {
    resultType.countVars(countersMap);
    paramTypes.forEach(p -> p.countVars(countersMap));
  }

  public TypeN resType() {
    return resType;
  }

  public ImmutableList<TypeN> paramTypes() {
    return paramTypes;
  }
}
