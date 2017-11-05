package org.smoothbuild.lang.type;

import org.smoothbuild.lang.value.Array;

public class ArrayType extends Type {
  private final Type elemType;

  protected ArrayType(Type elemType) {
    super("[" + elemType.name() + "]", Array.class);
    this.elemType = elemType;
  }

  public Type elemType() {
    return elemType;
  }
}
