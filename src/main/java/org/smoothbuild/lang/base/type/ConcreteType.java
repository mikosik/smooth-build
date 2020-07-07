package org.smoothbuild.lang.base.type;

public abstract class ConcreteType extends Type {
  private final ConcreteType superType;

  public ConcreteType(String name, ConcreteType superType) {
    super(name);
    this.superType = superType;
  }

  @Override
  public boolean isGeneric() {
    return false;
  }

  @Override
  public boolean isNothing() {
    return this == Types.nothing();
  }

  @Override
  public ConcreteType superType() {
    return superType;
  }

  @Override
  public ConcreteType coreType() {
    return this;
  }

  @Override
  public ConcreteType changeCoreDepthBy(int delta) {
    if (delta < 0) {
      throw new IllegalArgumentException(
          "It's not possible to reduce core depth of non array type.");
    }
    ConcreteType result = this;
    for (int i = 0; i < delta; i++) {
      result = new ConcreteArrayType(result);
    }
    return result;
  }

  @Override
  public boolean isAssignableFrom(IType type) {
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
      ConcreteType
          thisElemType = ((ConcreteArrayType) this).elemType();
      ConcreteType
          thatElemType = ((ConcreteArrayType) type).elemType();
      return thisElemType.isAssignableFrom(thatElemType);
    }
    if (type instanceof StructType) {
      return isAssignableFrom(((StructType) type).superType());
    }
    return false;
  }

  @Override
  public boolean isParamAssignableFrom(IType type) {
    return isAssignableFrom(type);
  }
}
