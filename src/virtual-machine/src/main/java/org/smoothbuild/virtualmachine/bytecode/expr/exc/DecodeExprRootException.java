package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.BKind;

public class DecodeExprRootException extends DecodeExprException {
  public static DecodeExprRootException cannotReadRootException(Hash hash, Throwable cause) {
    return new DecodeExprRootException(hash, "Cannot decode root.", cause);
  }

  public static DecodeExprRootException wrongSizeOfRootChainException(Hash hash, int actualSize) {
    return new DecodeExprRootException(
        hash,
        "Its root points to hash sequence with " + actualSize
            + " elems when it should point to sequence with 1 or 2 elems.");
  }

  public static DecodeExprRootException wrongSizeOfRootChainException(
      Hash hash, BKind kind, int actualSize) {
    return new DecodeExprRootException(
        hash,
        "Its root points to hash sequence with " + actualSize
            + " elements. First element is " + kind.name() + " kind which means "
            + rootShouldPointToSequence(kind));
  }

  private static String rootShouldPointToSequence(BKind kind) {
    if (kind.containsData()) {
      return "its root should point to sequence of 2 elements.";
    } else {
      return "its root should point to sequence of 1 element.";
    }
  }

  private DecodeExprRootException(Hash hash, String message) {
    this(hash, message, null);
  }

  private DecodeExprRootException(Hash hash, String message, Throwable cause) {
    super("Cannot decode object at " + hash + ". " + message, cause);
  }
}
