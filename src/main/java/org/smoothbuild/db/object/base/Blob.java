package org.smoothbuild.db.object.base;

import static org.smoothbuild.db.object.db.Helpers.wrapDecodingObjectException;

import org.smoothbuild.db.object.db.ObjectDb;

import okio.BufferedSource;

/**
 * This class is immutable.
 */
public class Blob extends Val {
  public Blob(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  public BufferedSource source() {
    return wrapDecodingObjectException(hash(), () -> hashedDb().source(dataHash()));
  }

  @Override
  public String valueToString() {
    return "0x??";
  }
}

