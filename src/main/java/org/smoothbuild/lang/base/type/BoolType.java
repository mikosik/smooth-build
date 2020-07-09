package org.smoothbuild.lang.base.type;

import org.smoothbuild.lang.object.base.Bool;

public class BoolType extends ConcreteBasicType {
  public BoolType() {
    super("Bool", Bool.class);
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
