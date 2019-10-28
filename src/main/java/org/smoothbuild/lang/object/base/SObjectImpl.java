package org.smoothbuild.lang.object.base;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.object.db.ObjectsDbException.objectsDbException;

import java.io.IOException;
import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.type.ConcreteType;

public class SObjectImpl implements SObject {
  private final Hash hash;
  private final Hash dataHash;
  private final ConcreteType type;
  protected final HashedDb hashedDb;

  public SObjectImpl(Hash dataHash, ConcreteType type, HashedDb hashedDb) {
    this(calculateHash(type, dataHash, hashedDb), dataHash, type, hashedDb);
  }

  public SObjectImpl(Hash hash, Hash dataHash, ConcreteType type, HashedDb hashedDb) {
    this.hash = checkNotNull(hash);
    this.dataHash = checkNotNull(dataHash);
    this.type = type;
    this.hashedDb = checkNotNull(hashedDb);
  }

  private static Hash calculateHash(ConcreteType type, Hash dataHash, HashedDb hashedDb) {
    try {
      return hashedDb.writeHashes(type.hash(), dataHash);
    } catch (IOException e) {
      throw objectsDbException(e);
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
