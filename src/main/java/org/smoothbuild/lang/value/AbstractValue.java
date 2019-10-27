package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.db.values.ValuesDbException.valuesDbException;

import java.io.IOException;
import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.type.ConcreteType;

public class AbstractValue implements Value {
  private final Hash hash;
  private final Hash dataHash;
  private final ConcreteType type;
  protected final HashedDb hashedDb;

  public AbstractValue(Hash dataHash, ConcreteType type, HashedDb hashedDb) {
    this(calculateHash(type, dataHash, hashedDb), dataHash, type, hashedDb);
  }

  public AbstractValue(Hash hash, Hash dataHash, ConcreteType type, HashedDb hashedDb) {
    this.hash = checkNotNull(hash);
    this.dataHash = checkNotNull(dataHash);
    this.type = type;
    this.hashedDb = checkNotNull(hashedDb);
  }

  private static Hash calculateHash(ConcreteType type, Hash dataHash, HashedDb hashedDb) {
    try {
      return hashedDb.writeHashes(type.hash(), dataHash);
    } catch (IOException e) {
      throw valuesDbException(e);
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
    return object instanceof Value && equals((Value) object);
  }

  private boolean equals(Value value) {
    return Objects.equals(hash, value.hash());
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
