package org.smoothbuild.object.err;

import com.google.common.hash.HashCode;

public class TooFewBytesToUnmarshallValue extends ObjectDbError {
  public TooFewBytesToUnmarshallValue(HashCode hash, String valueName, int size, int read) {
    super("Corrupted " + hash + " object. Value " + valueName + " has expected size = " + size
        + " but only " + read + " is available.");
  }
}
