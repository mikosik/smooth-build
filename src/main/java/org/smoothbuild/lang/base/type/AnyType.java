package org.smoothbuild.lang.base.type;

/**
 * This class is immutable.
 */
public class AnyType extends BaseType {
  public AnyType() {
    super(TypeNames.ANY);
  }

  @Override
  protected boolean isAssignableFrom(Type type, boolean variableRenaming) {
    return true;
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
