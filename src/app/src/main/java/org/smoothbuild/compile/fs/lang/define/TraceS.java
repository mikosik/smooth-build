package org.smoothbuild.compile.fs.lang.define;

import static java.util.Objects.requireNonNullElse;

import java.util.Objects;

import org.smoothbuild.compile.fs.lang.base.location.Location;

import com.google.common.base.Strings;

/**
 * Smooth stack trace.
 */
public final class TraceS {
  private final Element rootElement;

  public TraceS() {
    this.rootElement = null;
  }

  public TraceS(String name, Location location) {
    this(name, location, new TraceS());
  }

  public TraceS(String name, Location location, TraceS tail) {
    this.rootElement = new Element(name, location, tail.rootElement);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof TraceS that && Objects.equals(rootElement, that.rootElement);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rootElement);
  }

  @Override
  public String toString() {
    return rootElement == null ? "" : rootElement.toString();
  }

  private static record Element(String name, Location location, Element tail) {
    @Override
    public String toString() {
      return toString(nameMaxWidth() + 1);
    }

    private String toString(int padding) {
      var line = "@ " + Strings.padEnd(Objects.toString(name, ""), padding, ' ')
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
}
