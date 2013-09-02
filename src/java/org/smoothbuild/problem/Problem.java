package org.smoothbuild.problem;

import static com.google.common.base.Preconditions.checkNotNull;

public class Problem {
  private final ProblemType type;
  private final String message;

  public Problem(ProblemType type, String message) {
    this.type = checkNotNull(type);
    this.message = checkNotNull(message);
  }

  public ProblemType type() {
    return type;
  }

  public String message() {
    return message;
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof Problem) {
      Problem that = (Problem) object;
      return this.type == that.type && this.message.equals(that.message);
    }
    return false;
  }

  @Override
  public final int hashCode() {
    return this.type.hashCode() + 17 * message.hashCode();
  }

  @Override
  public String toString() {
    return type.toString() + ": " + message;
  }
}
