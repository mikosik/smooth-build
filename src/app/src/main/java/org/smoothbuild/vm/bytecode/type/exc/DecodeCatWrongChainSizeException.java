package org.smoothbuild.vm.bytecode.type.exc;

import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.CategoryKindB;

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
