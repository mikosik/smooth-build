package org.smoothbuild.common.log.report;

import java.util.Objects;

public class Trace {
  private final TraceLine topLine;

  public Trace() {
    this(null);
  }

  public Trace(TraceLine topLine) {
    this.topLine = topLine;
  }

  public TraceLine topLine() {
    return topLine;
  }

  @Override
  public boolean equals(Object obj) {
    return this == obj || (obj instanceof Trace that && Objects.equals(this.topLine, that.topLine));
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(topLine);
  }

  @Override
  public String toString() {
    var builder = new StringBuilder();
    var currentLine = topLine;
    while (currentLine != null) {
      builder.append(currentLine);
      currentLine = currentLine.next();
      if (currentLine != null) {
        builder.append("\n");
      }
    }
    return builder.toString();
  }
}
