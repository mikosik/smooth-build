package org.smoothbuild.lang.base.type;

public class TypeType extends ConcreteBasicType {
  public TypeType() {
    super("Type", org.smoothbuild.lang.object.type.TypeType.class);
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
