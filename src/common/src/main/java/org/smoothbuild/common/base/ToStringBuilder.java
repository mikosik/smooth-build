package org.smoothbuild.common.base;

import static org.smoothbuild.common.base.Strings.indent;

import java.util.Objects;
import org.smoothbuild.common.collect.List;

public class ToStringBuilder {
  private final StringBuilder stringBuilder;

  public ToStringBuilder(String name) {
    this.stringBuilder = new StringBuilder(name + "(\n");
  }

  public ToStringBuilder addField(String name, Object value) {
    appendLine(indent("  ", name + " = " + value));
    return this;
  }

  public ToStringBuilder addListField(String name, List<?> list) {
    appendLine("  " + name + " = [");
    for (var element : list) {
      appendLine(indent("    ", Objects.toString(element)));
    }
    appendLine("  ]");
    return this;
  }

  private void appendLine(String line) {
    stringBuilder.append(line);
    stringBuilder.append("\n");
  }

  @Override
  public String toString() {
    return stringBuilder + ")";
  }
}
