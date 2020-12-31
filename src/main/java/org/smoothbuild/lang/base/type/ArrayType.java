package org.smoothbuild.lang.base.type;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.lang.base.Location.internal;

import java.util.Objects;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class ArrayType extends Type {
  private final Type elemType;

  public ArrayType(Type elemType) {
    super("[" +  elemType.name() + "]", internal(), elemType.isPolytype());
    this.elemType = requireNonNull(elemType);
  }

  @Override
  public String typeConstructor() {
    return "[]";
  }

  public Type elemType() {
    return elemType;
  }

  @Override
  public ImmutableList<Type> covariants() {
    return ImmutableList.of(elemType);
  }

  @Override
  public Type mapTypeVariables(VariableToBounds variableToBounds,
      Side side) {
    return new ArrayType(elemType.mapTypeVariables(variableToBounds, side));
  }

  @Override
  protected boolean isAssignableFrom(Type type, boolean variableRenaming) {
    return (type instanceof NothingType) || isAssignableArray(type, variableRenaming);
  }

  private boolean isAssignableArray(Type type, boolean variableRenaming) {
    return type instanceof ArrayType thatArrayType
        && elemType().isAssignableFrom(thatArrayType.elemType(), variableRenaming);
  }

  @Override
  public VariableToBounds inferVariableBounds(Type type, Side side) {
    if (type instanceof ArrayType thatArrayType) {
      return elemType.inferVariableBounds(thatArrayType.elemType(), side);
    } else if (type instanceof NothingType) {
      return elemType.inferVariableBounds(type, side);
    } else {
      return VariableToBounds.empty();
    }
  }

  @Override
  public Type mergeWith(Type that, Side direction) {
    if (that instanceof ArrayType thatArray) {
      return new ArrayType(elemType.mergeWith(thatArray.elemType, direction));
    } else {
      return super.mergeWith(that, direction);
    }
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object instanceof ArrayType thatArray) {
      return this.elemType().equals(thatArray.elemType());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name());
  }
}
