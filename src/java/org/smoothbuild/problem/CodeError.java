package org.smoothbuild.problem;

public class CodeError extends CodeProblem {
  public CodeError(CodeLocation codeLocation, String message) {
    super(ProblemType.ERROR, codeLocation, message);
  }
}
