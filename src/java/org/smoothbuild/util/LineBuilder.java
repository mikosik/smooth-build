package org.smoothbuild.util;

public class LineBuilder {
  private final StringBuilder builder;

  public LineBuilder() {
    this.builder = new StringBuilder();
  }

  public void add(String string) {
    builder.append(string);
  }

  public void addLine(String string) {
    builder.append(string);
    builder.append("\n");
  }

  public String build() {
    return builder.toString();
  }
}
