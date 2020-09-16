package org.smoothbuild.lang.base.type;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.property.TypeProperties;

/**
 * Subclasses of this class must be immutable.
 */
public abstract class ConcreteType extends Type {
  private final ConcreteType superType;

  public ConcreteType(String name, Location location, ConcreteType superType,
      TypeProperties properties) {
    super(name, location, properties);
    this.superType = superType;
  }

  @Override
  public boolean isGeneric() {
    return false;
  }

  @Override
  public ConcreteType superType() {
    return superType;
  }

  @Override
  public boolean isAssignableFrom(Type type) {
    if (type.isGeneric()) {
      return false;
    }
    if (this.equals(type)) {
      return true;
    }
    if (type.isNothing()) {
      return true;
    }
    if (this instanceof ConcreteArrayType thisConcreteType
        && type instanceof ConcreteArrayType thatConcreteType) {
      ConcreteType thisElemType = thisConcreteType.elemType();
      ConcreteType thatElemType = thatConcreteType.elemType();
      return thisElemType.isAssignableFrom(thatElemType);
    }
    if (type instanceof StructType structType) {
      return isAssignableFrom(structType.superType());
    }
    return false;
  }

  @Override
  public boolean isParamAssignableFrom(Type type) {
    return isAssignableFrom(type);
  }
}
