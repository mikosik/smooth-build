package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeO;

public class UnexpectedObjSequenceException extends DecodeObjNodeException {
  public UnexpectedObjSequenceException(Hash hash, TypeO type, String path, int expectedSize,
      int actualSize) {
    super(hash, type, path, "Node is a sequence with wrong size. Expected " + expectedSize
        + " but was " + actualSize + ".");
  }
}
