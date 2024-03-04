package org.smoothbuild.virtualmachine.evaluate.execute;

import java.util.Objects;
import org.smoothbuild.common.base.Hash;

public final class TraceB {
  private final Element elements;

  public TraceB() {
    this.elements = null;
  }

  public TraceB(Hash call, Hash called) {
    this(call, called, new TraceB());
  }

  public TraceB(Hash call, Hash called, TraceB tail) {
    this.elements = new Element(call, called, tail.elements);
  }

  public Element elements() {
    return elements;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof TraceB that && Objects.equals(elements, that.elements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(elements);
  }

  @Override
  public String toString() {
    return elements == null ? "" : elements.toString();
  }

  public static record Element(Hash call, Hash called, Element tail) {
    @Override
    public String toString() {
      var line = call.toString() + " " + called.toString();
      if (tail == null) {
        return line;
      } else {
        return line + "\n" + tail;
      }
    }
  }
}
