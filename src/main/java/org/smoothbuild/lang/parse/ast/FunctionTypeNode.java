package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.util.collect.CountersMap;

import com.google.common.collect.ImmutableList;

public class FunctionTypeNode extends TypeNode {
  private final TypeNode resultType;
  private final ImmutableList<TypeNode> parameterTypes;

  public FunctionTypeNode(
      TypeNode resultType, ImmutableList<TypeNode> parameterTypes, Location location) {
    super("[" + resultType.name() + "]", location);
    this.resultType = resultType;
    this.parameterTypes = parameterTypes;
  }

  @Override
  public boolean isPolytype() {
    return resultType.isPolytype() || parameterTypes.stream().anyMatch(TypeNode::isPolytype);
  }

  @Override
  public void countVariables(CountersMap<String> countersMap) {
    countFunctionVariables(countersMap, resultType, parameterTypes);
  }

  public static void countFunctionVariables(CountersMap<String> countersMap, TypeNode resultType,
      ImmutableList<TypeNode> parameterTypes) {
    resultType.countVariables(countersMap);
    parameterTypes.forEach(p -> p.countVariables(countersMap));
  }

  public TypeNode resultType() {
    return resultType;
  }

  public ImmutableList<TypeNode> parameterTypes() {
    return parameterTypes;
  }
}
