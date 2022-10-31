package org.smoothbuild.compile.lang.define;

import static java.util.Objects.requireNonNullElse;

import java.util.Objects;

import org.smoothbuild.compile.lang.base.Loc;

import com.google.common.base.Strings;

/**
 * Smooth stack trace.
 */
public record TraceS(String name, Loc loc, TraceS tail) {
  public TraceS(String tag, Loc loc) {
    this(tag, loc, null);
  }

  @Override
  public String toString() {
    return toString(nameMaxWidth() + 1);
  }

  private String toString(int padding) {
    var line =  "@ " + Strings.padEnd(Objects.toString(name, ""), padding, ' ') + loc.toString();
    if (tail == null) {
      return line;
    } else {
      return line + "\n" + tail.toString(padding);
    }
  }

  private int nameMaxWidth() {
    int length = requireNonNullElse(name, "").length();
    return tail == null ? length : Math.max(length, tail.nameMaxWidth());
  }
}
