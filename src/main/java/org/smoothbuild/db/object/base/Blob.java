package org.smoothbuild.db.object.base;

import static org.smoothbuild.db.object.db.Helpers.wrapDecodingObjectException;

import org.smoothbuild.db.hashed.HashedDb;

import okio.BufferedSource;

/**
 * This class is immutable.
 */
public class Blob extends Obj {
  public Blob(MerkleRoot merkleRoot, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
  }

  public BufferedSource source() {
    return wrapDecodingObjectException(hash(), () -> hashedDb.source(dataHash()));
  }

  @Override
  public String valueToString() {
    return "0x??";
  }
}

