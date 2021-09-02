package org.smoothbuild.db.object.db;

import org.smoothbuild.db.hashed.Hash;

public class DecodeDataHashSequenceException extends ObjectDbException {
  public DecodeDataHashSequenceException(Hash hash, int expectedSize, int actualSize) {
    super("Cannot read data hash sequence at " + hash + ". Expected size = " + expectedSize + " "
        + "actual size = " + actualSize + ".");
  }
}
