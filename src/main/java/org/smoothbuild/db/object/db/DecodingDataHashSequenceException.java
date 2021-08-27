package org.smoothbuild.db.object.db;

import org.smoothbuild.db.hashed.Hash;

public class DecodingDataHashSequenceException extends ObjectDbException {
  public DecodingDataHashSequenceException(Hash hash, int expectedSize, int actualSize) {
    super("Cannot read data hash sequence at " + hash + ". Expected size = " + expectedSize + " "
        + "actual size = " + actualSize + ".");
  }
}
