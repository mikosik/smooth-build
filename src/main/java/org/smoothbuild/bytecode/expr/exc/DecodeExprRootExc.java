package org.smoothbuild.bytecode.expr.exc;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.CatB;

public class DecodeExprRootExc extends DecodeExprExc {
  public static DecodeExprRootExc cannotReadRootException(Hash hash, Throwable cause) {
    return new DecodeExprRootExc(hash, "Cannot decode root.", cause);
  }

  public static DecodeExprRootExc wrongSizeOfRootSeqException(Hash hash, int actualSize) {
    return new DecodeExprRootExc(hash, "Its root points to hash sequence with " + actualSize
        + " elems when it should point to sequence with 1 or 2 elems.");
  }

  public static DecodeExprRootExc wrongSizeOfRootSeqException(
      Hash hash, CatB category, int actualSize) {
    return new DecodeExprRootExc(hash, "Its root points to hash sequence with " + actualSize
        + " elements. First element is " + category.name() + " category which means "
        + (category.containsData() ? "its root should point to sequence of 2 elements." :
        "its root should point to sequence of 1 element.")
        );
  }

  private DecodeExprRootExc(Hash hash, String message) {
    this(hash, message, null);
  }

  private DecodeExprRootExc(Hash hash, String message, Throwable cause) {
    super("Cannot decode object at " + hash + ". " + message, cause);
  }
}
