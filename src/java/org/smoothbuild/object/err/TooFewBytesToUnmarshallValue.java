package org.smoothbuild.object.err;

import com.google.common.hash.HashCode;

public class TooFewBytesToUnmarshallValue extends ObjectDbError {
  public TooFewBytesToUnmarshallValue(HashCode hash, String valueName, int size, int read) {
    super("Cannot unmarshall " + valueName + "value from " + hash + ", expected " + size
        + " bytes but only " + read + " is available.");
  }
}
