package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Location;

public class FieldNode extends NamedNode {
  private final TypeNode type;

  public FieldNode(TypeNode type, String name, Location location) {
    super(name, location);
    this.type = type;
  }

  public TypeNode type() {
    return type;
  }
}
