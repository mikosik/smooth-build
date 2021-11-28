package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;

public class DecodeObjRootException extends DecodeObjException {
  public static DecodeObjRootException cannotReadRootException(Hash hash, Throwable cause) {
    return new DecodeObjRootException(hash, "Cannot decode root.", cause);
  }

  public static DecodeObjRootException wrongSizeOfRootSequenceException(Hash hash, int actualSize) {
    return new DecodeObjRootException(hash, "Its root points to hash sequence with " + actualSize
        + " elems when it should point to sequence with 2 elems.");
  }

  private DecodeObjRootException(Hash hash, String message) {
    this(hash, message, null);
  }

  private DecodeObjRootException(Hash hash, String message, Throwable cause) {
    super("Cannot decode object at " + hash + ". " + message, cause);
  }
}
