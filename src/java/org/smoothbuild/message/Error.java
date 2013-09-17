package org.smoothbuild.message;

import static org.smoothbuild.message.MessageType.ERROR;

public class Error extends Message {
  public Error(String message) {
    super(ERROR, message);
  }
}
