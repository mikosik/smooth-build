package org.smoothbuild.object.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.Message;

public class IllegalPathInObjectError extends Message {
  public IllegalPathInObjectError(String message) {
    super(ERROR, "Objects database corrupted: reading path failed with: " + message);
  }
}
