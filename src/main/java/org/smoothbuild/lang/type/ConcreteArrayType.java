package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.value.Array;

import com.google.common.hash.HashCode;

public class ConcreteArrayType extends ConcreteType implements ArrayType {
  private final ConcreteType elemType;
  private final ValuesDb valuesDb;

  public ConcreteArrayType(HashCode dataHash, TypeType type, ConcreteArrayType superType,
      ConcreteType elemType, HashedDb hashedDb, ValuesDb valuesDb) {
    super(dataHash, type, superType, "[" + elemType.name() + "]", Array.class, hashedDb, valuesDb);
    this.elemType = checkNotNull(elemType);
    this.valuesDb = checkNotNull(valuesDb);
  }

  @Override
  public Array newValue(HashCode dataHash) {
    return new Array(dataHash, this, valuesDb, hashedDb);
  }

  @Override
  public ConcreteType elemType() {
    return elemType;
  }

  @Override
  public ConcreteType coreType() {
    return elemType.coreType();
  }

  @Override
  public <T extends Type> T replaceCoreType(T coreType) {
    return (T) coreType.changeCoreDepthBy(coreDepth());
  }

  @Override
  public int coreDepth() {
    return 1 + elemType.coreDepth();
  }

  @Override
  public ConcreteType changeCoreDepthBy(int delta) {
    if (delta < 0) {
      return elemType.changeCoreDepthBy(delta + 1);
    } else {
      return super.changeCoreDepthBy(delta);
    }
  }
}
