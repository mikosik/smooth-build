package org.smoothbuild.db.record.base;

import static org.smoothbuild.db.record.db.Helpers.wrapDecodingRecordException;

import org.smoothbuild.db.hashed.HashedDb;

/**
 * This class is immutable.
 */
public class Bool extends Record {
  public Bool(MerkleRoot merkleRoot, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
  }

  public boolean jValue() {
    return wrapDecodingRecordException(hash(), () -> hashedDb.readBoolean(dataHash()));
  }

  @Override
  public String valueToString() {
    return Boolean.toString(jValue());
  }
}
