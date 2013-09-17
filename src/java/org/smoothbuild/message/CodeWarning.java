package org.smoothbuild.message;

import static org.smoothbuild.message.MessageType.WARNING;

public class CodeWarning extends CodeMessage {
  public CodeWarning(CodeLocation codeLocation, String message) {
    super(WARNING, codeLocation, message);
  }
}
