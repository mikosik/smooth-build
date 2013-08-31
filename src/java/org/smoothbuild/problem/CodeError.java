package org.smoothbuild.problem;

public class CodeError extends CodeProblem {
  public CodeError(SourceLocation sourceLocation, String message) {
    super(ProblemType.ERROR, sourceLocation, message);
  }
}
