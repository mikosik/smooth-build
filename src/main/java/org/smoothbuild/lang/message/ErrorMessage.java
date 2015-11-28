package org.smoothbuild.lang.message;

import static org.smoothbuild.lang.message.MessageType.ERROR;

public class ErrorMessage extends Message {
  public ErrorMessage(String message) {
    super(ERROR, message);
  }
}
