package org.smoothbuild.compile.lang.define;

import java.util.Objects;

import org.smoothbuild.compile.lang.base.Loc;

/**
 * Smooth stack trace.
 */
public record TraceS(String name, Loc loc, TraceS tail) {
  public TraceS(String tag, Loc loc) {
    this(tag, loc, null);
  }

  @Override
  public String toString() {
    var line = Objects.toString(name, "") + " " + loc.toString();
    if (tail == null) {
      return line;
    } else {
      return line + "\n" + tail;
    }
  }
}
