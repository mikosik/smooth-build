package org.smoothbuild.parse.err;

import static org.smoothbuild.problem.ProblemType.ERROR;

import org.smoothbuild.problem.Problem;
import org.smoothbuild.problem.SourceLocation;

public class SyntaxError extends Problem {
  public SyntaxError(SourceLocation sourceLocation, String message) {
    super(ERROR, sourceLocation, message);
  }
}
