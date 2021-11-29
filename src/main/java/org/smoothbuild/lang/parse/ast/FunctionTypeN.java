package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.util.collect.CountersMap;

import com.google.common.collect.ImmutableList;

public final class FunctionTypeN extends TypeN {
  private final TypeN resultType;
  private final ImmutableList<TypeN> paramTypes;

  public FunctionTypeN(
      TypeN resultType, ImmutableList<TypeN> paramTypes, Location location) {
    super("[" + resultType.name() + "]", location);
    this.resultType = resultType;
    this.paramTypes = paramTypes;
  }

  @Override
  public boolean isPolytype() {
    return resultType.isPolytype() || paramTypes.stream().anyMatch(TypeN::isPolytype);
  }

  @Override
  public void countVariables(CountersMap<String> countersMap) {
    countFunctionVariables(countersMap, resultType, paramTypes);
  }

  public static void countFunctionVariables(CountersMap<String> countersMap, TypeN resultType,
      ImmutableList<TypeN> paramTypes) {
    resultType.countVariables(countersMap);
    paramTypes.forEach(p -> p.countVariables(countersMap));
  }

  public TypeN resultType() {
    return resultType;
  }

  public ImmutableList<TypeN> paramTypes() {
    return paramTypes;
  }
}
