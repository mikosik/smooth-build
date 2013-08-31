package org.smoothbuild.parse.err;

import org.smoothbuild.problem.CodeError;
import org.smoothbuild.problem.SourceLocation;

public class SyntaxError extends CodeError {
  public SyntaxError(SourceLocation sourceLocation, String message) {
    super(sourceLocation, message);
  }
}
