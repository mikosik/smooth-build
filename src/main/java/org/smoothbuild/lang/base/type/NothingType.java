package org.smoothbuild.lang.base.type;

/**
 * This class is immutable.
 */
public class NothingType extends ConcreteBasicType {
  public NothingType() {
    super(TypeNames.NOTHING);
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
