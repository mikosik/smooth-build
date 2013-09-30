package org.smoothbuild.parse.err;

import org.smoothbuild.message.message.ErrorCodeMessage;
import org.smoothbuild.message.message.CodeLocation;

public class SyntaxError extends ErrorCodeMessage {
  public SyntaxError(CodeLocation codeLocation, String message) {
    super(codeLocation, message);
  }
}
