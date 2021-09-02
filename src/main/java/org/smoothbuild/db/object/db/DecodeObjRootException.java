package org.smoothbuild.db.object.db;

import org.smoothbuild.db.hashed.Hash;

public class DecodeObjRootException extends DecodeObjException {
  public DecodeObjRootException(Hash hash, int actualSize) {
    this(hash, "Its root points to hash sequence with " + actualSize
        + " elements when it should point to sequence with 1 or 2 elements.");
  }

  public static DecodeObjRootException nullObjRootException(Hash hash, int actualSize) {
    return new DecodeObjRootException(hash, "Its root points to hash sequence with " + actualSize
        + " elements when it should point to sequence with 1 element as its spec is NULL.");
  }

  public static DecodeObjRootException nonNullObjRootException(Hash hash, int actualSize) {
    return new DecodeObjRootException(hash, "Its root points to hash sequence with " + actualSize
        + " elements when it should point to sequence with 2 elements as its spec is not NULL.");
  }

  private DecodeObjRootException(Hash hash, String message) {
    super(hash, message);
  }
}
