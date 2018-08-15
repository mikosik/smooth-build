package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.value.Array;

import com.google.common.hash.HashCode;

public class ConcreteArrayType extends ConcreteType implements ArrayType {
  private final ConcreteType elemType;
  private final Instantiator instantiator;

  protected ConcreteArrayType(HashCode dataHash, TypeType type, ConcreteArrayType superType,
      ConcreteType elemType, Instantiator instantiator, HashedDb hashedDb, TypesDb typesDb) {
    super(dataHash, type, superType, "[" + elemType.name() + "]", Array.class, hashedDb, typesDb);
    this.elemType = checkNotNull(elemType);
    this.instantiator = checkNotNull(instantiator);
  }

  @Override
  public Array newValue(HashCode dataHash) {
    return new Array(dataHash, this, instantiator, hashedDb);
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
    return (T) coreType.increaseCoreDepthBy(coreDepth());
  }

  @Override
  public int coreDepth() {
    return 1 + elemType.coreDepth();
  }

  @Override
  public ConcreteType decreaseCoreDepthBy(int delta) {
    if (delta == 0) {
      return this;
    } else {
      return elemType.decreaseCoreDepthBy(delta - 1);
    }
  }
}
