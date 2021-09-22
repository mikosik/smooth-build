package org.smoothbuild.db.object.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.base.SpecKind;

public class UnexpectedSpecSequenceException extends DecodeSpecNodeException {
  public UnexpectedSpecSequenceException(Hash hash, SpecKind spec, String path, int expectedSize,
      int actualSize) {
    super(hash, spec, path, "Node is a sequence with wrong size. Expected " + expectedSize
        + " but was " + actualSize + ".");
  }
}
