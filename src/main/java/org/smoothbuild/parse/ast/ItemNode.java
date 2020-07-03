package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Location;

public class ItemNode extends NamedNode {
  private final int index;
  private final TypeNode typeNode;
  private final ExprNode defaultValue;

  public ItemNode(int index, TypeNode typeNode, String name, ExprNode defaultValue,
      Location location) {
    super(name, location);
    this.index = index;
    this.typeNode = typeNode;
    this.defaultValue = defaultValue;
  }

  public int index() {
    return index;
  }

  public TypeNode typeNode() {
    return typeNode;
  }

  public ExprNode defaultValue() {
    return defaultValue;
  }

  public boolean hasDefaultValue() {
    return defaultValue != null;
  }
}
