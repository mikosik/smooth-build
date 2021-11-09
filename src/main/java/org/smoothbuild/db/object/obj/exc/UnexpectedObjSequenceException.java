package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeH;

public class UnexpectedObjSequenceException extends DecodeObjNodeException {
  public UnexpectedObjSequenceException(Hash hash, TypeH type, String path, int expectedSize,
      int actualSize) {
    super(hash, type, path, "Node is a sequence with wrong size. Expected " + expectedSize
        + " but was " + actualSize + ".");
  }
}
