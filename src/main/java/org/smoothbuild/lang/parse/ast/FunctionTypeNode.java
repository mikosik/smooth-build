package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.Location;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

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
  public ImmutableSet<TypeNode> variables() {
    Builder<TypeNode> builder = ImmutableSet.builder();
    builder.addAll(resultType.variables());
    parameterTypes.stream()
        .map(TypeNode::variables)
        .forEach(builder::addAll);
    return builder.build();
  }

  public TypeNode resultType() {
    return resultType;
  }

  public ImmutableList<TypeNode> parameterTypes() {
    return parameterTypes;
  }
}
