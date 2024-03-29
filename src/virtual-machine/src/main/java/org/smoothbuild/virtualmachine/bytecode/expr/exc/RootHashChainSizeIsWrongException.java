package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BKind;

public class RootHashChainSizeIsWrongException extends DecodeExprException {
  public static RootHashChainSizeIsWrongException cannotReadRootException(
      Hash hash, Throwable cause) {
    return new RootHashChainSizeIsWrongException(hash, "Cannot decode root.", cause);
  }

  public static RootHashChainSizeIsWrongException wrongSizeOfRootChainException(
      Hash hash, int actualSize) {
    return new RootHashChainSizeIsWrongException(
        hash,
        "Its root points to hash sequence with " + actualSize
            + " elems when it should point to sequence with 1 or 2 elems.");
  }

  public static RootHashChainSizeIsWrongException wrongSizeOfRootChainException(
      Hash hash, BKind kind, int actualSize) {
    return new RootHashChainSizeIsWrongException(
        hash,
        "Its root points to hash sequence with " + actualSize
            + " elements. First element is " + kind.name()
            + " kind which means its root should point to sequence of 2 elements.");
  }

  private RootHashChainSizeIsWrongException(Hash hash, String message) {
    this(hash, message, null);
  }

  private RootHashChainSizeIsWrongException(Hash hash, String message, Throwable cause) {
    super("Cannot decode expression at " + hash + ". " + message, cause);
  }
}
