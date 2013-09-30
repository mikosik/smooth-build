package org.smoothbuild.message.message;

import static org.smoothbuild.message.listen.MessageType.INFO;

public class Info extends Message {
  public Info(String message) {
    super(INFO, message);
  }
}
