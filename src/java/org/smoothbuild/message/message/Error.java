package org.smoothbuild.message.message;

import static org.smoothbuild.message.listen.MessageType.ERROR;

public class Error extends Message {
  public Error(String message) {
    super(ERROR, message);
  }
}
