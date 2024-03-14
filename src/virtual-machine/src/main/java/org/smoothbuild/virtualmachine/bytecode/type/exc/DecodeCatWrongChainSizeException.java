package org.smoothbuild.virtualmachine.bytecode.type.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryId;

public class DecodeCatWrongChainSizeException extends DecodeCatNodeException {
  public DecodeCatWrongChainSizeException(
      Hash hash, CategoryId categoryId, String path, int expectedSize, int actualSize) {
    super(hash, categoryId, path, message(expectedSize, actualSize));
  }

  private static String message(int expectedSize, int actualSize) {
    return "Node is a chain with wrong size. Expected " + expectedSize + " but was " + actualSize
        + ".";
  }
}
