package org.smoothbuild.db.object.type.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.ObjKind;

public class UnexpectedTypeSequenceException extends DecodeTypeNodeException {
  public UnexpectedTypeSequenceException(Hash hash, ObjKind kind, String path, int expectedSize,
      int actualSize) {
    super(hash, kind, path, "Node is a sequence with wrong size. Expected " + expectedSize
        + " but was " + actualSize + ".");
  }
}
