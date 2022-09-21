package org.smoothbuild.bytecode.type.exc;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.CategoryKindB;

public class DecodeCatWrongSeqSizeExc extends DecodeCatNodeExc {
  public DecodeCatWrongSeqSizeExc(Hash hash, CategoryKindB kind, String path, int expectedSize,
      int actualSize) {
    super(hash, kind, path, "Node is a sequence with wrong size. Expected " + expectedSize
        + " but was " + actualSize + ".");
  }
}
