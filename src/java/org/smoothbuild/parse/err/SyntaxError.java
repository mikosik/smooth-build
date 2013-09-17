package org.smoothbuild.parse.err;

import org.smoothbuild.message.CodeError;
import org.smoothbuild.message.CodeLocation;

public class SyntaxError extends CodeError {
  public SyntaxError(CodeLocation codeLocation, String message) {
    super(codeLocation, message);
  }
}
