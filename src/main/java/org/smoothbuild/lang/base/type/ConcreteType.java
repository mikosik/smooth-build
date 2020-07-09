package org.smoothbuild.lang.base.type;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.compound.Compoundability;

public abstract class ConcreteType extends Type {
  private final ConcreteType superType;

  public ConcreteType(String name, Location location, ConcreteType superType,
      Compoundability compoundability) {
    super(name, location, compoundability);
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
    if (this instanceof ConcreteArrayType && type instanceof ConcreteArrayType) {
      ConcreteType thisElemType = ((ConcreteArrayType) this).elemType();
      ConcreteType thatElemType = ((ConcreteArrayType) type).elemType();
      return thisElemType.isAssignableFrom(thatElemType);
    }
    if (type instanceof StructType) {
      return isAssignableFrom(((StructType) type).superType());
    }
    return false;
  }

  @Override
  public boolean isParamAssignableFrom(Type type) {
    return isAssignableFrom(type);
  }
}
