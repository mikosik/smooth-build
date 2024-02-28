package org.smoothbuild.vm.bytecode.expr.exc;

import org.smoothbuild.common.Hash;
import org.smoothbuild.vm.bytecode.type.CategoryB;

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
      Hash hash, CategoryB category, int actualSize) {
    return new DecodeExprRootException(
        hash,
        "Its root points to hash sequence with " + actualSize
            + " elements. First element is " + category.name() + " category which means "
            + rootShouldPointToSequence(category));
  }

  private static String rootShouldPointToSequence(CategoryB category) {
    if (category.containsData()) {
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
