package org.smoothbuild.lang.base.type;

public class StringType extends ConcreteBasicType {
  public StringType() {
    super(TypeNames.STRING);
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
