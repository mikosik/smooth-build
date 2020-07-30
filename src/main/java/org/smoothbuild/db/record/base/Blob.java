package org.smoothbuild.db.record.base;

import static org.smoothbuild.db.record.db.Helpers.wrapDecodingRecordException;

import org.smoothbuild.db.hashed.HashedDb;

import okio.BufferedSource;

/**
 * This class is immutable.
 */
public class Blob extends Record {
  public Blob(MerkleRoot merkleRoot, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
  }

  public BufferedSource source() {
    return wrapDecodingRecordException(hash(), () -> hashedDb.source(dataHash()));
  }

  @Override
  public String valueToString() {
    return "0x??";
  }
}

