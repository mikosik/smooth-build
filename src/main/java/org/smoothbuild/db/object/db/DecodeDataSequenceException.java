package org.smoothbuild.db.object.db;

import org.smoothbuild.db.hashed.Hash;

public class DecodeDataSequenceException extends DecodeObjException {
  public DecodeDataSequenceException(Hash hash, Hash dataHash, int expectedSize, int actualSize) {
    super(hash, "Its data points to hash sequence at " + dataHash + " which should have"
        + expectedSize + " elements but has " + actualSize + " elements.");
  }
}
