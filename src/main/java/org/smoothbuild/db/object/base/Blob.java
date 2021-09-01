package org.smoothbuild.db.object.base;

import static org.smoothbuild.db.object.db.Helpers.wrapHashedDbExceptionAsDecodeObjException;

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
    return wrapHashedDbExceptionAsDecodeObjException(hash(), () -> hashedDb().source(dataHash()));
  }

  @Override
  public String valueToString() {
    return "0x??";
  }
}

