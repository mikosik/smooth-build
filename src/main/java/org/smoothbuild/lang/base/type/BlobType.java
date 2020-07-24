package org.smoothbuild.lang.base.type;

public class BlobType extends ConcreteBasicType {
  public BlobType() {
    super(TypeNames.BLOB);
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
