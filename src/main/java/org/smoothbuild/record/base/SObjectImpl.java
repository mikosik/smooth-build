package org.smoothbuild.record.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.record.type.BinaryType;

/**
 * This class is immutable.
 */
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
  public BinaryType type() {
    return merkleRoot.type();
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof SObject
        && Objects.equals(hash(), ((SObject) object).hash());
  }

  @Override
  public int hashCode() {
    return hash().hashCode();
  }

  @Override
  public String toString() {
    return type().name() + "(" + valueToString() + "):" + hash();
  }

  protected String valueToString() {
    return "...";
  }
}
