package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.Location;

public class ParamNode extends Node {
  private final TypeNode type;
  private final Name name;
  private final ExprNode defaultValue;

  public ParamNode(TypeNode type, Name name, ExprNode defaultValue, Location location) {
    super(location);
    this.type = type;
    this.name = name;
    this.defaultValue = defaultValue;
  }

  public TypeNode type() {
    return type;
  }

  public Name name() {
    return name;
  }

  public ExprNode defaultValue() {
    return defaultValue;
  }

  public boolean hasDefaultValue() {
    return defaultValue != null;
  }
}
