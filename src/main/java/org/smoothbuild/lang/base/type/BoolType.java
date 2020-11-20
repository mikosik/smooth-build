package org.smoothbuild.lang.base.type;

/**
 * This class is immutable.
 */
public class BoolType extends BaseType {
  public BoolType() {
    super(TypeNames.BOOL);
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
