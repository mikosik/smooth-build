package org.smoothbuild.message;

import static org.smoothbuild.message.MessageType.WARNING;

public class Warning extends Message {
  public Warning(String message) {
    super(WARNING, message);
  }
}
