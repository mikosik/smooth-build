package org.smoothbuild.lang.base.type;

/**
 * This class is immutable.
 */
public class NothingType extends BaseType {
  public NothingType() {
    super(TypeNames.NOTHING);
  }

  @Override
  public Type mergeWith(Type that, Side direction) {
    return switch (direction) {
      case UPPER -> that;
      case LOWER -> this;
    };
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
