package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.lang.base.type.constraint.Bounds.oneSideBound;
import static org.smoothbuild.lang.base.type.constraint.Side.LOWER;

import org.smoothbuild.lang.base.type.constraint.Side;
import org.smoothbuild.lang.base.type.constraint.VariableToBounds;

/**
 * This class is immutable.
 */
public class TypeVariable extends Type {
  public TypeVariable(String name) {
    super(name, internal(), true);
  }

  @Override
  public Type mapTypeVariables(VariableToBounds variableToBounds) {
    return variableToBounds.boundsMap().get(this).get(LOWER);
  }

  @Override
  public boolean isAssignableFrom(Type type, boolean variableRenaming) {
    return (type instanceof NothingType) || variableRenaming || equals(type);
  }

  @Override
  public VariableToBounds inferVariableBounds(Type type, Side side) {
    return VariableToBounds.empty().addBounds(this, oneSideBound(side, type));
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
