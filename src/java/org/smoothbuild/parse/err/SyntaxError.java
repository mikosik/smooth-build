package org.smoothbuild.parse.err;

import org.smoothbuild.problem.Error;
import org.smoothbuild.problem.SourceLocation;

public class SyntaxError extends Error {
  public SyntaxError(SourceLocation sourceLocation, String message) {
    super(sourceLocation, message);
  }
}
