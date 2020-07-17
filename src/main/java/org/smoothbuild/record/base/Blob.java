package org.smoothbuild.record.base;

import static org.smoothbuild.record.db.Helpers.wrapException;

import org.smoothbuild.db.hashed.HashedDb;

import okio.BufferedSource;

/**
 * This class is immutable.
 */
public class Blob extends SObjectImpl {
  public Blob(MerkleRoot merkleRoot, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
  }

  public BufferedSource source() {
    return wrapException(hash(), () -> hashedDb.source(dataHash()));
  }
}
