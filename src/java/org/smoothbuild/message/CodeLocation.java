package org.smoothbuild.message;

import static com.google.common.base.Preconditions.checkArgument;

public class CodeLocation {
  private final int line;
  private final int start;
  private final int end;

  public static CodeLocation codeLocation(int line, int start, int end) {
    return new CodeLocation(line, start, end);
  }

  private CodeLocation(int line, int start, int end) {
    checkArgument(0 <= line);
    checkArgument(0 <= start);
    checkArgument(0 <= end);

    this.line = line;
    this.start = start;
    this.end = end;
  }

  /**
   * Line in build script. (first line = 0)
   */
  public int line() {
    return line;
  }

  /**
   * Start position within line. (first character in line = 0)
   */
  public int start() {
    return start;
  }

  /**
   * End position within line. (first character in line = 0)
   */
  public int end() {
    return end;
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof CodeLocation) {
      CodeLocation that = (CodeLocation) object;
      return this.line == that.line && this.start == that.start && this.end == that.end;
    }
    return false;
  }

  @Override
  public final int hashCode() {
    return line + 17 * (start + 17 * (end));
  }

  @Override
  public String toString() {
    return "[" + str(line + 1) + ":" + str(start + 1) + "-" + str(end + 1) + "]";
  }

  private static String str(int integer) {
    return Integer.toString(integer);
  }
}
