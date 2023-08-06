package org.smoothbuild.virtualmachine.bytecode.type.exc;

import org.smoothbuild.common.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB;

public class DecodeCatWrongChainSizeException extends DecodeCatNodeException {
  public DecodeCatWrongChainSizeException(
      Hash hash, CategoryKindB kind, String path, int expectedSize, int actualSize) {
    super(hash, kind, path, message(expectedSize, actualSize));
  }

  private static String message(int expectedSize, int actualSize) {
    return "Node is a chain with wrong size. Expected " + expectedSize + " but was " + actualSize
        + ".";
  }
}
