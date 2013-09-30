package org.smoothbuild.message.message;

import static org.smoothbuild.message.listen.MessageType.INFO;

public class InfoMessage extends Message {
  public InfoMessage(String message) {
    super(INFO, message);
  }
}
