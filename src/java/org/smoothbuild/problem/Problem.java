package org.smoothbuild.problem;

public class Problem {
  private final ProblemType type;
  private final SourceLocation sourceLocation;
  private final String message;

  public Problem(ProblemType type, SourceLocation sourceLocation, String message) {
    this.type = type;
    this.sourceLocation = sourceLocation;
    this.message = message;
  }

  public ProblemType type() {
    return type;
  }

  public SourceLocation sourceLocation() {
    return sourceLocation;
  }

  public String message() {
    return message;
  }
}
