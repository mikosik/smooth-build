package org.smoothbuild.lang.type;

import org.smoothbuild.lang.value.Array;

public class GenericArrayType extends GenericType implements ArrayType {
  private final GenericType elemType;

  public GenericArrayType(GenericType elemType) {
    super("[" + elemType.name() + "]", Array.class);
    this.elemType = elemType;
  }

  @Override
  public <T extends Type> T actualCoreTypeWhenAssignedFrom(T type) {
    if (type.isArray()) {
      return (T) elemType.actualCoreTypeWhenAssignedFrom(((ArrayType) type).elemType());
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
    return (T) coreType.increaseCoreDepthBy(coreDepth());
  }

  @Override
  public int coreDepth() {
    return 1 + elemType.coreDepth();
  }

  @Override
  public GenericType decreaseCoreDepthBy(int delta) {
    if (delta == 0) {
      return this;
    } else {
      return elemType.decreaseCoreDepthBy(delta - 1);
    }
  }
}
