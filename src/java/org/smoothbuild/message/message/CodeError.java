package org.smoothbuild.message.message;

import org.smoothbuild.message.listen.MessageType;

@SuppressWarnings("serial")
public class CodeError extends CodeMessage {
  public CodeError(CodeLocation codeLocation, String message) {
    super(MessageType.ERROR, codeLocation, message);
  }
}
