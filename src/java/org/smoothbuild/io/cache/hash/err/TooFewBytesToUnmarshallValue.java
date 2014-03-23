package org.smoothbuild.io.cache.hash.err;

import com.google.common.hash.HashCode;

@SuppressWarnings("serial")
public class TooFewBytesToUnmarshallValue extends HashedDbError {
  public TooFewBytesToUnmarshallValue(HashCode hash, String valueName, int size, int read) {
    super("Corrupted " + hash + " object. Value " + valueName + " has expected size = " + size
        + " but only " + read + " is available.");
  }
}
