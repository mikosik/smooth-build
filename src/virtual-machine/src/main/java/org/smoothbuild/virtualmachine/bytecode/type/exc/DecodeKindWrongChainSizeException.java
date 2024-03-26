package org.smoothbuild.virtualmachine.bytecode.type.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.base.KindId;

public class DecodeKindWrongChainSizeException extends DecodeKindNodeException {
  public DecodeKindWrongChainSizeException(
      Hash hash, KindId kindId, String path, int expectedSize, int actualSize) {
    super(hash, kindId, path, message(expectedSize, actualSize));
  }

  private static String message(int expectedSize, int actualSize) {
    return "Node is a chain with wrong size. Expected " + expectedSize + " but was " + actualSize
        + ".";
  }
}
