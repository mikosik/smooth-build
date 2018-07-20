package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.type.ConcreteType;

import com.google.common.hash.HashCode;

public class AbstractValue implements Value {
  private final HashCode hash;
  private final HashCode dataHash;
  private final ConcreteType type;
  protected final HashedDb hashedDb;

  public AbstractValue(HashCode dataHash, ConcreteType type, HashedDb hashedDb) {
    this(calculateHash(type, dataHash, hashedDb), dataHash, type, hashedDb);
  }

  public AbstractValue(HashCode hash, HashCode dataHash, ConcreteType type, HashedDb hashedDb) {
    this.hash = checkNotNull(hash);
    this.dataHash = checkNotNull(dataHash);
    this.type = type;
    this.hashedDb = checkNotNull(hashedDb);
  }

  private static HashCode calculateHash(ConcreteType type, HashCode dataHash, HashedDb hashedDb) {
    return hashedDb.writeHashes(type.hash(), dataHash);
  }

  @Override
  public HashCode hash() {
    return hash;
  }

  @Override
  public HashCode dataHash() {
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
