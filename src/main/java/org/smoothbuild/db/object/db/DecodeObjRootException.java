package org.smoothbuild.db.object.db;

import org.smoothbuild.db.hashed.Hash;

public class DecodeObjRootException extends DecodeObjException {
  public DecodeObjRootException(Hash hash, int actualSize) {
    super(hash, "Its root points to hash sequence with " + actualSize
        + " elements when it should point to sequence with 2 elements.");
  }
}
