package org.smoothbuild.parse.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.message.message.CodeMessage;

public class SyntaxError extends CodeMessage {
  public SyntaxError(CodeLocation codeLocation, String message) {
    super(ERROR, codeLocation, message);
  }
}
