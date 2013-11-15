package org.smoothbuild.message.base;

import static com.google.common.base.Preconditions.checkArgument;

public class CodeLocation {
  private final int line;

  public static CodeLocation codeLocation(int line) {
    return new CodeLocation(line);
  }

  protected CodeLocation(int line) {
    checkArgument(0 < line);
    this.line = line;
  }

  /**
   * Line in build script. (first line = 1)
   */
  public int line() {
    return line;
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof CodeLocation) {
      CodeLocation that = (CodeLocation) object;
      return this.line == that.line;
    }
    return false;
  }

  @Override
  public final int hashCode() {
    return line;
  }

  @Override
  public String toString() {
    return "[ line " + Integer.toString(line) + " ]";
  }
}
