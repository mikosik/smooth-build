package org.smoothbuild.db.record.base;

import static org.smoothbuild.db.record.db.Helpers.wrapException;

import org.smoothbuild.db.hashed.HashedDb;

/**
 * This class is immutable.
 */
public class Bool extends RecordImpl {
  public Bool(MerkleRoot merkleRoot, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
  }

  public boolean jValue() {
    return wrapException(hash(), () -> hashedDb.readBoolean(dataHash()));
  }

  @Override
  public String valueToString() {
    return Boolean.toString(jValue());
  }
}
