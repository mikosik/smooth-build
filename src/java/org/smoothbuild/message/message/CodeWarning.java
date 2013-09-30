package org.smoothbuild.message.message;

import static org.smoothbuild.message.listen.MessageType.WARNING;

public class CodeWarning extends CodeMessage {
  public CodeWarning(CodeLocation codeLocation, String message) {
    super(WARNING, codeLocation, message);
  }
}
