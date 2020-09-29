package org.smoothbuild.lang.base.type;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.lang.base.Location.internal;

import java.util.Map;
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
  public Type mapTypeParameters(Map<GenericBasicType, Type> map) {
    return new ArrayType(elemType.mapTypeParameters(map));
  }

  @Override
  public Map<GenericBasicType, Type> inferTypeParametersMap(Type source) {
    if (source instanceof ArrayType arrayType) {
      return elemType.inferTypeParametersMap(arrayType.elemType());
    } else if (source.isNothing()) {
      return elemType.inferTypeParametersMap(source);
    } else {
      throw new IllegalArgumentException("Cannot assign " + q() + " from " + source.q());
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
  public boolean isParamAssignableFrom(Type type) {
    if (isGeneric()) {
      return type.isNothing() || (type instanceof ArrayType thatArrayType
          && elemType.isParamAssignableFrom(thatArrayType.elemType));
    } else {
      return isAssignableFrom(type);
    }
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
