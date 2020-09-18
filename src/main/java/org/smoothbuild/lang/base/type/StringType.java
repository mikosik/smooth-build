package org.smoothbuild.lang.base.type;

/**
 * This class is immutable.
 */
public class StringType extends BasicType {
  public StringType() {
    super(TypeNames.STRING);
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
