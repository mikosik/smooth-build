package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.message.Location;

public class ParamNode extends NamedNode {
  private final TypeNode type;
  private final ExprNode defaultValue;

  public ParamNode(TypeNode type, String name, ExprNode defaultValue, Location location) {
    super(name, location);
    this.type = type;
    this.defaultValue = defaultValue;
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
