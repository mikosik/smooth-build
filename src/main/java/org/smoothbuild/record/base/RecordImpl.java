package org.smoothbuild.record.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.record.spec.Spec;

/**
 * This class is immutable.
 */
public class RecordImpl implements Record {
  private final MerkleRoot merkleRoot;
  protected final HashedDb hashedDb;

  public RecordImpl(MerkleRoot merkleRoot, HashedDb hashedDb) {
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
  public Spec spec() {
    return merkleRoot.spec();
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Record
        && Objects.equals(hash(), ((Record) object).hash());
  }

  @Override
  public int hashCode() {
    return hash().hashCode();
  }

  @Override
  public String toString() {
    return spec().name() + "(" + valueToString() + "):" + hash();
  }

  protected String valueToString() {
    return "...";
  }
}
