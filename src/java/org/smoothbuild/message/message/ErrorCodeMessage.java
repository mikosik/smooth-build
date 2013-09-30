package org.smoothbuild.message.message;

import org.smoothbuild.message.listen.MessageType;

public class ErrorCodeMessage extends CodeMessage {
  public ErrorCodeMessage(CodeLocation codeLocation, String message) {
    super(MessageType.ERROR, codeLocation, message);
  }
}
