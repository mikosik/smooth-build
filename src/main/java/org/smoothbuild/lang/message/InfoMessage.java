package org.smoothbuild.lang.message;

import static org.smoothbuild.lang.message.MessageType.INFO;

public class InfoMessage extends Message {
  public InfoMessage(String message) {
    super(INFO, message);
  }
}
