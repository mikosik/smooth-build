package org.smoothbuild.lang.base.type;

/**
 * This class is immutable.
 */
public class GenericBasicType extends BasicType {
  public GenericBasicType(String name) {
    super(name, true);
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
