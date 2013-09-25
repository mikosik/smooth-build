package org.smoothbuild.message;

import static org.smoothbuild.message.MessageType.ERROR;

@SuppressWarnings("serial")
public class Error extends Message {
  public Error(String message) {
    super(ERROR, message);
  }
}
