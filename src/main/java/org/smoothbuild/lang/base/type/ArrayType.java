package org.smoothbuild.lang.base.type;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.lang.base.Location.internal;

import java.util.Objects;

import org.smoothbuild.lang.base.type.constraint.Constraints;
import org.smoothbuild.lang.base.type.constraint.Side;

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
  public Type mapTypeVariables(Constraints constraints) {
    return new ArrayType(elemType.mapTypeVariables(constraints));
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
  public Constraints inferConstraints(Type type, Side side) {
    if (type instanceof ArrayType thatArrayType) {
      return elemType.inferConstraints(thatArrayType.elemType(), side);
    } else if (type instanceof NothingType) {
      return elemType.inferConstraints(type, side);
    } else {
      return Constraints.empty();
    }
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
