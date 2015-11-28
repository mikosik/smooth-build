package org.smoothbuild.lang.message;

import static org.smoothbuild.lang.message.MessageType.WARNING;

public class WarningMessage extends Message {
  public WarningMessage(String message) {
    super(WARNING, message);
  }
}
