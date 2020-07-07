package org.smoothbuild.lang.base.type;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.base.type.compound.ArrayCompoundability;

public class GenericArrayType extends GenericType implements ArrayType {
  private final GenericType elemType;

  public GenericArrayType(GenericType elemType) {
    super("[" +  elemType.name() + "]", new ArrayCompoundability());
    this.elemType = requireNonNull(elemType);
  }

  @Override
  public GenericType elemType() {
    return elemType;
  }
}
