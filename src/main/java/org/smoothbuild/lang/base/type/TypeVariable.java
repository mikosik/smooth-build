package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.lang.base.type.Bounds.oneSideBound;

/**
 * This class is immutable.
 */
public class TypeVariable extends Type {
  public TypeVariable(String name) {
    super(name, internal(), true);
  }

  @Override
  public Type mapTypeVariables(VariableToBounds variableToBounds, Side side) {
    return variableToBounds.boundsMap().get(this).get(side);
  }

  @Override
  public boolean isAssignableFrom(Type type, boolean variableRenaming) {
    return (type instanceof NothingType) || variableRenaming || equals(type);
  }

  @Override
  public VariableToBounds inferVariableBounds(Type that, Side side) {
    return VariableToBounds.empty().addBounds(this, oneSideBound(side, that));
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
