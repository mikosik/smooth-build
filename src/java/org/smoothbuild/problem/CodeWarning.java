package org.smoothbuild.problem;

import static org.smoothbuild.problem.ProblemType.WARNING;

public class CodeWarning extends CodeProblem {
  public CodeWarning(SourceLocation sourceLocation, String message) {
    super(WARNING, sourceLocation, message);
  }
}
