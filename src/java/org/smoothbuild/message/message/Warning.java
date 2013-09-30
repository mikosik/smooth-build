package org.smoothbuild.message.message;

import static org.smoothbuild.message.listen.MessageType.WARNING;

public class Warning extends Message {
  public Warning(String message) {
    super(WARNING, message);
  }
}
