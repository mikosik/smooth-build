package org.smoothbuild.db.object.exc;

import org.smoothbuild.db.hashed.Hash;

public class DecodeSpecRootException extends DecodeSpecException {
  public DecodeSpecRootException(Hash hash, int actualSize) {
    super(hash, "Its root points to hash sequence with " + actualSize
        + " elements when it should point to sequence with 1 or 2 elements.");
  }
}
