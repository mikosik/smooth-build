package org.smoothbuild.problem;

import static org.smoothbuild.problem.ProblemType.WARNING;

public class Warning extends Problem {
  public Warning(String message) {
    super(WARNING, message);
  }
}
