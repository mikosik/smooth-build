package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.message.Location;

public class ArrayTypeNode extends TypeNode {
  private final TypeNode elementType;

  public ArrayTypeNode(TypeNode elementType, Location location) {
    super("[" + elementType.name() + "]", location);
    this.elementType = elementType;
  }

  public TypeNode elementType() {
    return elementType;
  }
}
