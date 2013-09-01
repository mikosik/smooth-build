package org.smoothbuild.parse.err;

import org.smoothbuild.problem.CodeError;
import org.smoothbuild.problem.CodeLocation;

public class SyntaxError extends CodeError {
  public SyntaxError(CodeLocation codeLocation, String message) {
    super(codeLocation, message);
  }
}
