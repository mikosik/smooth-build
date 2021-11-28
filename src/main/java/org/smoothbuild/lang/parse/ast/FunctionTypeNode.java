package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.util.collect.CountersMap;

import com.google.common.collect.ImmutableList;

public final class FunctionTypeNode extends TypeNode {
  private final TypeNode resultType;
  private final ImmutableList<TypeNode> paramTypes;

  public FunctionTypeNode(
      TypeNode resultType, ImmutableList<TypeNode> paramTypes, Location location) {
    super("[" + resultType.name() + "]", location);
    this.resultType = resultType;
    this.paramTypes = paramTypes;
  }

  @Override
  public boolean isPolytype() {
    return resultType.isPolytype() || paramTypes.stream().anyMatch(TypeNode::isPolytype);
  }

  @Override
  public void countVariables(CountersMap<String> countersMap) {
    countFunctionVariables(countersMap, resultType, paramTypes);
  }

  public static void countFunctionVariables(CountersMap<String> countersMap, TypeNode resultType,
      ImmutableList<TypeNode> paramTypes) {
    resultType.countVariables(countersMap);
    paramTypes.forEach(p -> p.countVariables(countersMap));
  }

  public TypeNode resultType() {
    return resultType;
  }

  public ImmutableList<TypeNode> paramTypes() {
    return paramTypes;
  }
}
