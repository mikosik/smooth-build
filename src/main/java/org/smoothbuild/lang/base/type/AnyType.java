package org.smoothbuild.lang.base.type;

/**
 * This class is immutable.
 */
public class AnyType extends BaseType {
  public AnyType() {
    super(TypeNames.ANY);
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
