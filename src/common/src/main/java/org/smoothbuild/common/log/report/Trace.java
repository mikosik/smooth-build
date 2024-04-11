package org.smoothbuild.common.log.report;

import java.util.Objects;

public class Trace<T extends TraceLine<T>> {
  private final T topLine;

  public Trace(T topLine) {
    this.topLine = topLine;
  }

  public T topLine() {
    return topLine;
  }

  @Override
  public boolean equals(Object obj) {
    return this == obj
        || (obj instanceof Trace<?> that && Objects.equals(this.topLine, that.topLine));
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

  public record Node<T extends TraceLine<T>>(TraceLine<T> traceLine, Node<T> next) {}
}
