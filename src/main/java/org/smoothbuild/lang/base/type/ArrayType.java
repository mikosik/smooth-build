package org.smoothbuild.lang.base.type;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class ArrayType extends Type {
  private final Type elemType;

  public ArrayType(Type elemType) {
    super("[" +  elemType.name() + "]", createTypeConstructor(), elemType.isPolytype());
    this.elemType = requireNonNull(elemType);
  }

  private static TypeConstructor createTypeConstructor() {
    return new TypeConstructor("[]", 1, 0,
        (covariants, contravariants) -> new ArrayType(covariants.get(0)));
  }

  public Type elemType() {
    return elemType;
  }

  @Override
  public ImmutableList<Type> covariants() {
    return ImmutableList.of(elemType);
  }

  @Override
  public Type mapVariables(VariableToBounds variableToBounds,
      Side side) {
    return new ArrayType(elemType.mapVariables(variableToBounds, side));
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
