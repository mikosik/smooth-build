package org.smoothbuild.lang.type;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.value.Array;

import com.google.common.hash.HashCode;

public class ArrayType extends Type {
  private final Type elemType;

  protected ArrayType(HashCode hash, TypeType type, ArrayType superType, Type elemType) {
    super(hash, type, superType, "[" + elemType.name() + "]", Array.class);
    this.elemType = elemType;
  }

  @Override
  public Array newValue(HashCode hash, HashedDb hashedDb) {
    return new Array(hash, this, hashedDb);
  }

  public Type elemType() {
    return elemType;
  }

  @Override
  public Type coreType() {
    return elemType.coreType();
  }

  @Override
  public int coreDepth() {
    return 1 + elemType.coreDepth();
  }
}
