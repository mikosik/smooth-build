package org.smoothbuild.compilerfrontend.lang.define;

import java.util.Objects;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;

/**
 * Smooth stack trace.
 */
public final class STrace {
  private final Element elements;

  public STrace() {
    this(null);
  }

  public STrace(Element headElement) {
    this.elements = headElement;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof STrace that && Objects.equals(elements, that.elements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(elements);
  }

  @Override
  public String toString() {
    return elements == null ? "" : elements.toString();
  }

  public static record Element(String called, Location location, Element tail) {
    public Element {
      Objects.requireNonNull(called);
      Objects.requireNonNull(location);
    }

    @Override
    public String toString() {
      var line = "@ " + location.toString() + " " + Objects.toString(called, "");
      if (tail == null) {
        return line;
      } else {
        return line + "\n" + tail;
      }
    }
  }
}
