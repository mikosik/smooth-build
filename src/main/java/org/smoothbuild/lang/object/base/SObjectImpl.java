package org.smoothbuild.lang.object.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.db.ObjectsDbException;
import org.smoothbuild.lang.object.db.ValuesDb;
import org.smoothbuild.lang.object.db.ValuesDbException;
import org.smoothbuild.lang.object.type.ConcreteType;

public class SObjectImpl implements SObject {
  private final Hash hash;
  private final Hash dataHash;
  private final ConcreteType type;
  protected final ValuesDb valuesDb;

  public SObjectImpl(Hash dataHash, ConcreteType type, ValuesDb valuesDb) {
    this(calculateHash(type, dataHash, valuesDb), dataHash, type, valuesDb);
  }

  public SObjectImpl(Hash hash, Hash dataHash, ConcreteType type, ValuesDb valuesDb) {
    this.hash = checkNotNull(hash);
    this.dataHash = checkNotNull(dataHash);
    this.type = type;
    this.valuesDb = checkNotNull(valuesDb);
  }

  private static Hash calculateHash(ConcreteType type, Hash dataHash, ValuesDb valuesDb) {
    try {
      return valuesDb.writeHashes(type.hash(), dataHash);
    } catch (ValuesDbException e) {
      throw new ObjectsDbException(e);
    }
  }

  @Override
  public Hash hash() {
    return hash;
  }

  @Override
  public Hash dataHash() {
    return dataHash;
  }

  @Override
  public ConcreteType type() {
    return type;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof SObject && equals((SObject) object);
  }

  private boolean equals(SObject object) {
    return Objects.equals(hash, object.hash());
  }

  @Override
  public int hashCode() {
    return hash.hashCode();
  }

  @Override
  public String toString() {
    return type.name() + "(...):" + hash();
  }
}
