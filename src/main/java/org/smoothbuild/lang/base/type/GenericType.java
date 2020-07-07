package org.smoothbuild.lang.base.type;

public abstract class GenericType extends Type {
  public GenericType(String name) {
    super(name);
  }

  public <T extends IType> T actualCoreTypeWhenAssignedFrom(T type) {
    return type;
  }

  @Override
  public boolean isGeneric() {
    return true;
  }

  @Override
  public GenericType superType() {
    return null;
  }

  @Override
  public GenericType coreType() {
    return this;
  }

  @Override
  public GenericType changeCoreDepthBy(int delta) {
    if (delta < 0) {
      throw new IllegalArgumentException(
          "It's not possible to reduce core depth of non array type.");
    }
    GenericType result = this;
    for (int i = 0; i < delta; i++) {
      result = new GenericArrayType(result);
    }
    return result;
  }

  @Override
  public boolean isAssignableFrom(IType type) {
    if (type.isGeneric()) {
      return equals(type);
    } else {
      return type.coreType().isNothing() && type.coreDepth() <= coreDepth();
    }
  }

  @Override
  public boolean isParamAssignableFrom(IType type) {
    if (type.coreType().isNothing()) {
      return true;
    }
    return coreDepth() <= type.coreDepth();
  }
}
