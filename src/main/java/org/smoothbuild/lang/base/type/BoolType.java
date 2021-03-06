package org.smoothbuild.lang.base.type;

public class BoolType extends ConcreteBasicType {
  public BoolType() {
    super(TypeNames.BOOL);
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
