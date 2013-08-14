package org.smoothbuild.problem;

import static org.smoothbuild.problem.ProblemType.WARNING;

public class Warning extends Problem {
  public Warning(SourceLocation sourceLocation, String message) {
    super(WARNING, sourceLocation, message);
  }
}
