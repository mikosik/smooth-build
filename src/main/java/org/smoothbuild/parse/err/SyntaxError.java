package org.smoothbuild.parse.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.CodeMessage;

public class SyntaxError extends CodeMessage {
  public SyntaxError(CodeLocation codeLocation, String message) {
    super(ERROR, codeLocation, message);
  }
}
