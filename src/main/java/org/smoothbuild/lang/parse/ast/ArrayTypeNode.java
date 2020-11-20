package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.Location;

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
  public TypeNode coreType() {
    return elementType.coreType();
  }

  public TypeNode elementType() {
    return elementType;
  }
}
