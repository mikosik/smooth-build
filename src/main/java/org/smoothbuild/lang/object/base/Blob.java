package org.smoothbuild.lang.object.base;

import static org.smoothbuild.lang.object.db.Helpers.wrapException;

import org.smoothbuild.db.hashed.HashedDb;

import okio.BufferedSource;

public class Blob extends SObjectImpl {
  public Blob(MerkleRoot merkleRoot, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
  }

  public BufferedSource source() {
    return wrapException(hash(), () -> hashedDb.source(dataHash()));
  }
}
