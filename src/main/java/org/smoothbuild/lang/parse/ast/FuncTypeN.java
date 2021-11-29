package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.util.collect.CountersMap;

import com.google.common.collect.ImmutableList;

public final class FuncTypeN extends TypeN {
  private final TypeN resultType;
  private final ImmutableList<TypeN> paramTypes;

  public FuncTypeN(
      TypeN resultType, ImmutableList<TypeN> paramTypes, Loc loc) {
    super("[" + resultType.name() + "]", loc);
    this.resultType = resultType;
    this.paramTypes = paramTypes;
  }

  @Override
  public boolean isPolytype() {
    return resultType.isPolytype() || paramTypes.stream().anyMatch(TypeN::isPolytype);
  }

  @Override
  public void countVars(CountersMap<String> countersMap) {
    countFuncVars(countersMap, resultType, paramTypes);
  }

  public static void countFuncVars(CountersMap<String> countersMap, TypeN resultType,
      ImmutableList<TypeN> paramTypes) {
    resultType.countVars(countersMap);
    paramTypes.forEach(p -> p.countVars(countersMap));
  }

  public TypeN resultType() {
    return resultType;
  }

  public ImmutableList<TypeN> paramTypes() {
    return paramTypes;
  }
}
