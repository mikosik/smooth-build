package org.smoothbuild.lang.base.type;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.lang.base.Location.internal;

import java.util.Map;
import java.util.Objects;

/**
 * This class is immutable.
 */
public class ArrayType extends Type {
  private final Type elemType;

  public ArrayType(Type elemType) {
    super("[" +  elemType.name() + "]", internal(), elemType.isPolytype());
    this.elemType = requireNonNull(elemType);
  }

  public Type elemType() {
    return elemType;
  }

  @Override
  public Type mapTypeVariables(Map<TypeVariable, Type> map) {
    return new ArrayType(elemType.mapTypeVariables(map));
  }

  @Override
  public Map<TypeVariable, Type> inferTypeVariables(Type source) {
    if (source instanceof ArrayType arrayType) {
      return elemType.inferTypeVariables(arrayType.elemType());
    } else if (source instanceof NothingType) {
      return elemType.inferTypeVariables(source);
    } else {
      throw new IllegalArgumentException("Cannot assign " + q() + " from " + source.q());
    }
  }

  @Override
  public boolean isAssignableFrom(Type type) {
    return (type instanceof NothingType)
        || (type instanceof ArrayType thatArrayType && elemTypesAreAssignable(thatArrayType));
  }

  private boolean elemTypesAreAssignable(ArrayType thatArrayType) {
    return elemType().isAssignableFrom(thatArrayType.elemType());
  }

  @Override
  public boolean isParamAssignableFrom(Type type) {
    return (type instanceof NothingType) || (type instanceof ArrayType thatArrayType
        && elemType.isParamAssignableFrom(thatArrayType.elemType));
  }

  @Override
  public Type joinWith(Type that) {
    if (that instanceof ArrayType thatArray) {
      return new ArrayType(elemType.joinWith(thatArray.elemType));
    } else {
      return super.joinWith(that);
    }
  }

  @Override
  public Type meetWith(Type that) {
    if (that instanceof ArrayType thatArray) {
      return new ArrayType(elemType.meetWith(thatArray.elemType));
    } else {
      return super.meetWith(that);
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
