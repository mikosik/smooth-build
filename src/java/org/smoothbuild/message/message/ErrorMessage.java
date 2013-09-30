package org.smoothbuild.message.message;

import static org.smoothbuild.message.listen.MessageType.ERROR;

public class ErrorMessage extends Message {
  public ErrorMessage(String message) {
    super(ERROR, message);
  }
}
