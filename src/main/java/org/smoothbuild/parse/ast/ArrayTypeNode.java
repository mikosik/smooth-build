package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.message.CodeLocation;

public class ArrayTypeNode extends TypeNode {
  private final TypeNode elementType;

  public ArrayTypeNode(TypeNode elementType, CodeLocation codeLocation) {
    super("[" + elementType.name() + "]", codeLocation);
    this.elementType = elementType;
  }

  public TypeNode elementType() {
    return elementType;
  }
}
