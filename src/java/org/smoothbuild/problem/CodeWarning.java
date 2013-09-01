package org.smoothbuild.problem;

import static org.smoothbuild.problem.ProblemType.WARNING;

public class CodeWarning extends CodeProblem {
  public CodeWarning(CodeLocation codeLocation, String message) {
    super(WARNING, codeLocation, message);
  }
}
