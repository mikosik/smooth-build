package org.smoothbuild.db.object.exc;

import org.smoothbuild.db.hashed.Hash;

public class DecodeObjRootException extends DecodeObjException {
  public static DecodeObjRootException nullObjRootException(Hash hash, int actualSize) {
    return new DecodeObjRootException(hash, "Its root points to hash sequence with " + actualSize
        + " elements when it should point to sequence with 1 element as its spec is NULL.");
  }

  public static DecodeObjRootException nonNullObjRootException(Hash hash, int actualSize) {
    return new DecodeObjRootException(hash, "Its root points to hash sequence with " + actualSize
        + " elements when it should point to sequence with 2 elements as its spec is not NULL.");
  }

  public static DecodeObjRootException cannotReadRootException(Hash hash, Throwable cause) {
    return new DecodeObjRootException(hash, "Cannot decode root.", cause);
  }

  public static DecodeObjRootException wrongSizeOfRootSequenceException(Hash hash, int actualSize) {
    return new DecodeObjRootException(hash, "Its root points to hash sequence with " + actualSize
        + " elements when it should point to sequence with 1 or 2 elements.");
  }

  private DecodeObjRootException(Hash hash, String message) {
    this(hash, message, null);
  }

  private DecodeObjRootException(Hash hash, String message, Throwable cause) {
    super("Cannot decode object at " + hash + ". " + message, cause);
  }
}
