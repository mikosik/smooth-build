package org.smoothbuild.problem;

import static com.google.common.base.Preconditions.checkArgument;

public class CodeLocation {
  private final int line;
  private final int startPosition;
  private final int endPosition;

  public static CodeLocation codeLocation(int line, int startPosition, int endPosition) {
    return new CodeLocation(line, startPosition, endPosition);
  }

  private CodeLocation(int line, int startPosition, int endPosition) {
    checkArgument(0 < line);
    checkArgument(0 <= startPosition);
    checkArgument(0 <= endPosition);

    this.line = line;
    this.startPosition = startPosition;
    this.endPosition = endPosition;
  }

  /**
   * Line in build script. (first line = 1)
   */
  public int line() {
    return line;
  }

  /**
   * Start position within line. (first character = 0)
   */
  public int startPosition() {
    return startPosition;
  }

  /**
   * End position within line. (first character = 0)
   */
  public int endPosition() {
    return endPosition;
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof CodeLocation) {
      CodeLocation that = (CodeLocation) object;
      return this.line == that.line && this.startPosition == that.startPosition
          && this.endPosition == that.endPosition;
    }
    return false;
  }

  @Override
  public final int hashCode() {
    return line + 17 * (startPosition + 17 * (endPosition));
  }

  @Override
  public String toString() {
    return "[" + str(line) + ":" + str(startPosition) + "-" + str(endPosition) + "]";
  }

  private static String str(int integer) {
    return Integer.toString(integer);
  }
}
