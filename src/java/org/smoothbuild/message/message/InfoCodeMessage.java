package org.smoothbuild.message.message;

import static org.smoothbuild.message.listen.MessageType.INFO;

public class InfoCodeMessage extends CodeMessage {
  public InfoCodeMessage(CodeLocation codeLocation, String message) {
    super(INFO, codeLocation, message);
  }
}
