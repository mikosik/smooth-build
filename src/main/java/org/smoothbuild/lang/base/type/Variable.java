package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.type.Bounds.oneSideBound;

/**
 * Type variable.
 *
 * This class is immutable.
 */
public class Variable extends Type {
  public Variable(String name) {
    super(name, new TypeConstructor(name), true);
  }

  @Override
  public BoundedVariables inferVariableBounds(Type that, Side side) {
    return BoundedVariables.empty().addBounds(this, oneSideBound(side, that));
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
