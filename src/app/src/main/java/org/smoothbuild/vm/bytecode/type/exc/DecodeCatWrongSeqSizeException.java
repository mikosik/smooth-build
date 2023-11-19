package org.smoothbuild.vm.bytecode.type.exc;

import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.CategoryKindB;

public class DecodeCatWrongSeqSizeException extends DecodeCatNodeException {
  public DecodeCatWrongSeqSizeException(
      Hash hash, CategoryKindB kind, String path, int expectedSize, int actualSize) {
    super(hash, kind, path, "Node is a sequence with wrong size. Expected " + expectedSize
        + " but was " + actualSize + ".");
  }
}
