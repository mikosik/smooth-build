package org.smoothbuild.message.message;

import static org.smoothbuild.message.listen.MessageType.INFO;

public class CodeInfo extends CodeMessage {
  public CodeInfo(CodeLocation codeLocation, String message) {
    super(INFO, codeLocation, message);
  }
}
