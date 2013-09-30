package org.smoothbuild.message.message;

import static org.smoothbuild.message.listen.MessageType.WARNING;

public class WarningMessage extends Message {
  public WarningMessage(String message) {
    super(WARNING, message);
  }
}
