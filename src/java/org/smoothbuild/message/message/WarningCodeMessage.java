package org.smoothbuild.message.message;

import static org.smoothbuild.message.listen.MessageType.WARNING;

public class WarningCodeMessage extends CodeMessage {
  public WarningCodeMessage(CodeLocation codeLocation, String message) {
    super(WARNING, codeLocation, message);
  }
}
