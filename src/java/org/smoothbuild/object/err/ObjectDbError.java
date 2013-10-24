package org.smoothbuild.object.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.Message;

public class ObjectDbError extends Message {
  public ObjectDbError(String message) {
    super(ERROR, "Internal error in smooth object DB:\n" + message);
  }
}
