package org.smoothbuild.compile.fs.lang.define;

import static java.util.Objects.requireNonNullElse;

import java.util.Objects;

import org.smoothbuild.compile.fs.lang.base.location.Location;

import com.google.common.base.Strings;

/**
 * Smooth stack trace.
 */
public record TraceS(String name, Location location, TraceS tail) {
  public TraceS(String tag, Location location) {
    this(tag, location, null);
  }

  @Override
  public String toString() {
    return toString(nameMaxWidth() + 1);
  }

  private String toString(int padding) {
    var line =  "@ " + Strings.padEnd(Objects.toString(name, ""), padding, ' ')
        + location.toString();
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
