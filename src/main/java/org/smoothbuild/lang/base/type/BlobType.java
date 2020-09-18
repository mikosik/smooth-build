package org.smoothbuild.lang.base.type;

/**
 * This class is immutable.
 */
public class BlobType extends BasicType {
  public BlobType() {
    super(TypeNames.BLOB);
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
