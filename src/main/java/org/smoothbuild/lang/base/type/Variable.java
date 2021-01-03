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
  public Type mapVariables(VariableToBounds variableToBounds, Side side) {
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
