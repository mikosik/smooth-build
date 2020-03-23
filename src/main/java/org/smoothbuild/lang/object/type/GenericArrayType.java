package org.smoothbuild.lang.object.type;

import org.smoothbuild.lang.object.base.Array;

public class GenericArrayType extends GenericType implements ArrayType {
  private final GenericType elemType;

  public GenericArrayType(GenericType elemType) {
    super("[" + elemType.name() + "]", Array.class);
    this.elemType = elemType;
  }

  @Override
  public <T extends Type> T actualCoreTypeWhenAssignedFrom(T type) {
    if (type.isArray()) {
      @SuppressWarnings("unchecked")
      T result = (T) elemType.actualCoreTypeWhenAssignedFrom(((ArrayType) type).elemType());
      return result;
    } else if (type.isNothing()) {
      return type;
    } else {
      throw new IllegalArgumentException("Cannot assign " + name() + " from " + type.name());
    }
  }

  @Override
  public GenericType elemType() {
    return elemType;
  }

  @Override
  public GenericType coreType() {
    return elemType.coreType();
  }

  @Override
  public <T extends Type> T replaceCoreType(T coreType) {
    @SuppressWarnings("unchecked")
    T result = (T) coreType.changeCoreDepthBy(coreDepth());
    return result;
  }

  @Override
  public int coreDepth() {
    return 1 + elemType.coreDepth();
  }

  @Override
  public GenericType changeCoreDepthBy(int delta) {
    if (delta < 0) {
      return elemType.changeCoreDepthBy(delta + 1);
    } else {
      return super.changeCoreDepthBy(delta);
    }
  }
}
