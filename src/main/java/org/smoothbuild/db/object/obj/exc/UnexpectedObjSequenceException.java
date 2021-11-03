package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.ObjType;

public class UnexpectedObjSequenceException extends DecodeObjNodeException {
  public UnexpectedObjSequenceException(Hash hash, ObjType type, String path, int expectedSize,
      int actualSize) {
    super(hash, type, path, "Node is a sequence with wrong size. Expected " + expectedSize
        + " but was " + actualSize + ".");
  }
}
