package org.smoothbuild.lang.base.type;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.lang.base.Location.internal;

import java.util.Objects;
import java.util.Optional;

/**
 * This class is immutable.
 */
public class ArrayType extends Type {
  private final Type elemType;

  public ArrayType(Type elemType) {
    super("[" +  elemType.name() + "]", internal(), elemType.isGeneric());
    this.elemType = requireNonNull(elemType);
  }

  public Type elemType() {
    return elemType;
  }

  @Override
  public boolean isArray() {
    return true;
  }

  @Override
  public Type coreType() {
    return elemType.coreType();
  }

  @Override
  public Type replaceCoreType(Type type) {
    return new ArrayType(elemType.replaceCoreType(type));
  }

  @Override
  public int coreDepth() {
    return 1 + elemType.coreDepth();
  }

  @Override
  public Type changeCoreDepthBy(int delta) {
    if (delta < 0) {
      return elemType.changeCoreDepthBy(delta + 1);
    } else {
      return increaseCoreDepth(delta);
    }
  }

  @Override
  public boolean isAssignableFrom(Type type) {
    return type.isNothing()
        || (type instanceof ArrayType thatArrayType && elemTypesAreAssignable(thatArrayType));
  }

  private boolean elemTypesAreAssignable(ArrayType thatArrayType) {
    return elemType().isAssignableFrom(thatArrayType.elemType());
  }

  @Override
  public Optional<Type> commonSuperType(Type that) {
    if (that.isNothing()) {
      return Optional.of(this);
    } else if (that instanceof ArrayType thatArray) {
      return elemType.commonSuperType(thatArray.elemType).map(ArrayType::new);
    } else {
      return Optional.empty();
    }
  }

  @Override
  public Type actualCoreTypeWhenAssignedFrom(Type source) {
    if (source.isArray()) {
      return elemType.actualCoreTypeWhenAssignedFrom(((ArrayType) source).elemType());
    } else if (source.isNothing()) {
      return source;
    } else {
      throw new IllegalArgumentException("Cannot assign " + this + " from " + source.name());
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
