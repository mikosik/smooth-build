package org.smoothbuild.problem;

import static org.smoothbuild.problem.ProblemType.ERROR;

public class Error extends Problem {
  public Error(SourceLocation sourceLocation, String message) {
    super(ERROR, sourceLocation, message);
  }
}
