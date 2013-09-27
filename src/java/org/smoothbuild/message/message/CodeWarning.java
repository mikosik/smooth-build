package org.smoothbuild.message.message;

import static org.smoothbuild.message.listen.MessageType.WARNING;

@SuppressWarnings("serial")
public class CodeWarning extends CodeMessage {
  public CodeWarning(CodeLocation codeLocation, String message) {
    super(WARNING, codeLocation, message);
  }
}
