package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Location;

import com.google.common.collect.ImmutableSet;

public class ArrayTypeNode extends TypeNode {
  private final TypeNode elementType;

  public ArrayTypeNode(TypeNode elementType, Location location) {
    super("[" + elementType.name() + "]", location);
    this.elementType = elementType;
  }

  @Override
  public boolean isPolytype() {
    return elementType.isPolytype();
  }

  @Override
  public ImmutableSet<TypeNode> variables() {
    return elementType.variables();
  }

  public TypeNode elementType() {
    return elementType;
  }
}
