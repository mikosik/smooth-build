package org.smoothbuild.problem;

import static com.google.common.base.Preconditions.checkNotNull;

public class CodeProblem extends Problem {
  private final SourceLocation sourceLocation;

  public CodeProblem(ProblemType type, SourceLocation sourceLocation, String message) {
    super(type, message);
    this.sourceLocation = checkNotNull(sourceLocation);
  }

  @Override
  public String toString() {
    return type().toString() + sourceLocation.toString() + ": " + message();
  }
}
