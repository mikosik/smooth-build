package org.smoothbuild.lang.object.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.type.ConcreteType;

public class SObjectImpl implements SObject {
  private final MerkleRoot merkleRoot;
  protected final HashedDb hashedDb;

  public SObjectImpl(MerkleRoot merkleRoot, HashedDb hashedDb) {
    this.merkleRoot = merkleRoot;
    this.hashedDb = checkNotNull(hashedDb);
  }

  @Override
  public Hash hash() {
    return merkleRoot.hash();
  }

  @Override
  public Hash dataHash() {
    return merkleRoot.dataHash();
  }

  @Override
  public ConcreteType type() {
    return merkleRoot.type();
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof SObject && equals((SObject) object);
  }

  private boolean equals(SObject object) {
    return Objects.equals(hash(), object.hash());
  }

  @Override
  public int hashCode() {
    return hash().hashCode();
  }

  @Override
  public String toString() {
    return type().name() + "(...):" + hash();
  }
}
