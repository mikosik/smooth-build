package org.smoothbuild.common.log.report;

import java.util.Objects;
import org.smoothbuild.common.log.location.Location;

public class Trace {
  private final TraceLine topLine;

  public Trace(String called, Location location) {
    this.topLine = new TraceLine(called, location, null);
  }

  public Trace() {
    this(null);
  }

  public Trace(TraceLine topLine) {
    this.topLine = topLine;
  }

  public TraceLine topLine() {
    return topLine;
  }

  public boolean isEmpty() {
    return topLine == null;
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
