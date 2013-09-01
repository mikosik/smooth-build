package org.smoothbuild.problem;

import static com.google.common.base.Preconditions.checkNotNull;

public class CodeProblem extends Problem {
  private final CodeLocation codeLocation;

  public CodeProblem(ProblemType type, CodeLocation codeLocation, String message) {
    super(type, message);
    this.codeLocation = checkNotNull(codeLocation);
  }

  @Override
  public String toString() {
    return type().toString() + codeLocation.toString() + ": " + message();
  }
}
