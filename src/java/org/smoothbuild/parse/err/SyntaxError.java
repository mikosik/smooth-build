package org.smoothbuild.parse.err;

import org.smoothbuild.message.message.CodeError;
import org.smoothbuild.message.message.CodeLocation;

@SuppressWarnings("serial")
public class SyntaxError extends CodeError {
  public SyntaxError(CodeLocation codeLocation, String message) {
    super(codeLocation, message);
  }
}
