package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.message.Location;

public class ArrayTypeNode extends TypeNode {
  private final TypeNode elementType;

  public ArrayTypeNode(TypeNode elementType, Location location) {
    super("[" + elementType.name() + "]", location);
    this.elementType = elementType;
  }

  @Override
  public boolean isGeneric() {
    return elementType.isGeneric();
  }

  public TypeNode elementType() {
    return elementType;
  }
}
