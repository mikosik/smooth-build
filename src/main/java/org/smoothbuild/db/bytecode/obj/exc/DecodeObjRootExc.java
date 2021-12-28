package org.smoothbuild.db.bytecode.obj.exc;

import org.smoothbuild.db.hashed.Hash;

public class DecodeObjRootExc extends DecodeObjExc {
  public static DecodeObjRootExc cannotReadRootException(Hash hash, Throwable cause) {
    return new DecodeObjRootExc(hash, "Cannot decode root.", cause);
  }

  public static DecodeObjRootExc wrongSizeOfRootSeqException(Hash hash, int actualSize) {
    return new DecodeObjRootExc(hash, "Its root points to hash sequence with " + actualSize
        + " elems when it should point to sequence with 2 elems.");
  }

  private DecodeObjRootExc(Hash hash, String message) {
    this(hash, message, null);
  }

  private DecodeObjRootExc(Hash hash, String message, Throwable cause) {
    super("Cannot decode object at " + hash + ". " + message, cause);
  }
}
