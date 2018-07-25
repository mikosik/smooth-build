package org.smoothbuild.lang.type;

import org.smoothbuild.lang.value.Array;

public class GenericArrayType extends GenericType implements ArrayType {
  private final GenericType elemType;

  public GenericArrayType(GenericType elemType) {
    super("[" + elemType.name() + "]", Array.class);
    this.elemType = elemType;
  }

  @Override
  public Type actualCoreTypeWhenAssignedFrom(Type type) {
    if (type.isArray()) {
      return this.elemType.actualCoreTypeWhenAssignedFrom(((ArrayType) type).elemType());
    } else {
      return null;
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
  public int coreDepth() {
    return 1 + elemType.coreDepth();
  }
}
