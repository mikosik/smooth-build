package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.value.Array;

import com.google.common.hash.HashCode;

public class ArrayType extends ConcreteType {
  private final ConcreteType elemType;
  private final Instantiator instantiator;

  protected ArrayType(HashCode dataHash, TypeType type, ArrayType superType,
      ConcreteType elemType, Instantiator instantiator, HashedDb hashedDb) {
    super(dataHash, type, superType, "[" + elemType.name() + "]", Array.class, hashedDb);
    this.elemType = checkNotNull(elemType);
    this.instantiator = checkNotNull(instantiator);
  }

  @Override
  public Array newValue(HashCode dataHash) {
    return new Array(dataHash, this, instantiator, hashedDb);
  }

  public ConcreteType elemType() {
    return elemType;
  }

  @Override
  public ConcreteType coreType() {
    return elemType.coreType();
  }

  @Override
  public int coreDepth() {
    return 1 + elemType.coreDepth();
  }
}
