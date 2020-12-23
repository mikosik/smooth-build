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
  public Type joinWith(Type that) {
    return this;
  }

  @Override
  public Type meetWith(Type that) {
    return that;
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
