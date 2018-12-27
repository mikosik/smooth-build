package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Location;

public class ParamNode extends NamedNode {
  private final int index;
  private final TypeNode type;
  private final ExprNode defaultValue;

  public ParamNode(int index, TypeNode type, String name, ExprNode defaultValue,
      Location location) {
    super(name, location);
    this.index = index;
    this.type = type;
    this.defaultValue = defaultValue;
  }

  public int index() {
    return index;
  }

  public TypeNode type() {
    return type;
  }

  public ExprNode defaultValue() {
    return defaultValue;
  }

  public boolean hasDefaultValue() {
    return defaultValue != null;
  }
}
