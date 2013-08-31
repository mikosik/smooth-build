package org.smoothbuild.problem;

import static org.smoothbuild.problem.ProblemType.ERROR;

public class Error extends Problem {
  public Error(String message) {
    super(ERROR, message);
  }
}
