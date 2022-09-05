package org.smoothbuild.bytecode.expr.exc;

import org.smoothbuild.db.Hash;

public class DecodeExprRootExc extends DecodeExprExc {
  public static DecodeExprRootExc cannotReadRootException(Hash hash, Throwable cause) {
    return new DecodeExprRootExc(hash, "Cannot decode root.", cause);
  }

  public static DecodeExprRootExc wrongSizeOfRootSeqException(Hash hash, int actualSize) {
    return new DecodeExprRootExc(hash, "Its root points to hash sequence with " + actualSize
        + " elems when it should point to sequence with 2 elems.");
  }

  private DecodeExprRootExc(Hash hash, String message) {
    this(hash, message, null);
  }

  private DecodeExprRootExc(Hash hash, String message, Throwable cause) {
    super("Cannot decode object at " + hash + ". " + message, cause);
  }
}
