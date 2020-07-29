package org.smoothbuild.db.record.base;

import static org.smoothbuild.db.record.db.Helpers.wrapException;

import org.smoothbuild.db.hashed.HashedDb;

import okio.BufferedSource;

/**
 * This class is immutable.
 */
public class Blob extends RecordImpl {
  public Blob(MerkleRoot merkleRoot, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
  }

  public BufferedSource source() {
    return wrapException(hash(), () -> hashedDb.source(dataHash()));
  }

  @Override
  public String valueToString() {
    return "0x??";
  }
}

