package org.smoothbuild.message;

import static org.smoothbuild.message.MessageType.INFO;

public class Info extends Message {
  public Info(String message) {
    super(INFO, message);
  }
}
