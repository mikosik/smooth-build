package org.smoothbuild.lang.base.type;

import java.util.Optional;

/**
 * This class is immutable.
 */
public class NothingType extends BaseType {
  public NothingType() {
    super(TypeNames.NOTHING);
  }

  @Override
  public Optional<Type> joinWith(Type that) {
    return Optional.of(that);
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
