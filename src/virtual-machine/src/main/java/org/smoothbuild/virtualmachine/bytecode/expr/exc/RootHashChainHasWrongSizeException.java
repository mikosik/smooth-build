package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BKind;

public class RootHashChainHasWrongSizeException extends DecodeExprException {
  private static final String CANNOT_DECODE_ROOT = "Cannot decode root.";

  public static RootHashChainHasWrongSizeException cannotReadRootException(Hash hash) {
    return new RootHashChainHasWrongSizeException(hash, CANNOT_DECODE_ROOT);
  }

  public static RootHashChainHasWrongSizeException cannotReadRootException(
      Hash hash, Throwable cause) {
    return new RootHashChainHasWrongSizeException(hash, CANNOT_DECODE_ROOT, cause);
  }

  public static RootHashChainHasWrongSizeException wrongSizeOfRootChainException(
      Hash hash, int actualSize) {
    return new RootHashChainHasWrongSizeException(
        hash,
        "Its root points to hash sequence with " + actualSize
            + " elems when it should point to sequence with 1 or 2 elems.");
  }

  public static RootHashChainHasWrongSizeException wrongSizeOfRootChainException(
      Hash hash, BKind kind, int actualSize) {
    return new RootHashChainHasWrongSizeException(
        hash,
        "Its root points to hash sequence with " + actualSize
            + " elements. First element is " + kind.name()
            + " kind which means its root should point to sequence of 2 elements.");
  }

  private RootHashChainHasWrongSizeException(Hash hash, String message) {
    super(message(hash, message));
  }

  private RootHashChainHasWrongSizeException(Hash hash, String message, Throwable cause) {
    super(message(hash, message), cause);
  }

  private static String message(Hash hash, String message) {
    return "Cannot decode expression at " + hash + ". " + message;
  }
}
