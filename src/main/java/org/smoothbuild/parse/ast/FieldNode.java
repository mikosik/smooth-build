package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Location;

public class FieldNode extends NamedNode {
  private final TypeNode typeNode;

  public FieldNode(TypeNode typeNode, String name, Location location) {
    super(name, location);
    this.typeNode = typeNode;
  }

  public TypeNode typeNode() {
    return typeNode;
  }
}
