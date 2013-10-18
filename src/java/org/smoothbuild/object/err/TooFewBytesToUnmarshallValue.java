package org.smoothbuild.object.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.Message;

import com.google.common.hash.HashCode;

public class TooFewBytesToUnmarshallValue extends Message {
  public TooFewBytesToUnmarshallValue(HashCode hash, String valueName, int size, int read) {
    super(ERROR, "Cannot unmarshall " + valueName + "value from " + hash + ", expected " + size
        + " bytes but only " + read + " is available.");
  }
}
