package org.smoothbuild.db.hashed.err;

import com.google.common.hash.HashCode;

public class TooFewBytesToUnmarshallValueException extends HashedDbException {
  public TooFewBytesToUnmarshallValueException(HashCode hash, String valueName, int size, int read) {
    super("Corrupted " + hash + " object. Value " + valueName + " has expected size = " + size
        + " but only " + read + " is available.");
  }
}
