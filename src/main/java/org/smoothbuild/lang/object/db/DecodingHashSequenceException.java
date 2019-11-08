package org.smoothbuild.lang.object.db;

import org.smoothbuild.db.hashed.Hash;

public class DecodingHashSequenceException extends ValuesDbException {
  public DecodingHashSequenceException(Hash hash) {
    super("Value at " + hash + " cannot be decoded as hash sequence.");
  }

  public DecodingHashSequenceException(Hash hash, int expectedSize, int actualSize) {
    super("Hash sequence at " + hash + " has unexpected size. Expected " + expectedSize + ", " +
        "actual " + actualSize + ".");
  }

  public DecodingHashSequenceException(Hash hash, int minExpectedSize, int maxExpectedSize,
      int actualSize) {
    super("Hash sequence at " + hash + " has unexpected size. Expected range " + minExpectedSize +
        ".." + maxExpectedSize + " actual " + actualSize + ".");
  }
}
