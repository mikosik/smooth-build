package org.smoothbuild.lang.type;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.value.Array;

import com.google.common.hash.HashCode;

public class ArrayType extends Type {
  private final Type elemType;

  public static ArrayType arrayOf(Type elemType) {
    return new ArrayType(elemType);
  }

  private ArrayType(Type elemType) {
    super("[" + elemType.name() + "]", Array.class);
    this.elemType = elemType;
  }

  @Override
  public Array newValue(HashCode hash, HashedDb hashedDb) {
    return new Array(this, hash, hashedDb);
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

  @Override
  public ArrayType directConvertibleTo() {
    Type elemConvertibleTo = elemType.directConvertibleTo();
    return elemConvertibleTo == null ? null : arrayOf(elemConvertibleTo);
  }
}
