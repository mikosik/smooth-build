package org.smoothbuild.lang.base.type;

public class NothingType extends ConcreteBasicType {
  public NothingType() {
    super(TypeNames.NOTHING);
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
