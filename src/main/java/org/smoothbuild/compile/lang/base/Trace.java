package org.smoothbuild.compile.lang.base;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

/**
 * Stack trace.
 */
public class Trace<T> {
  private final T elem;
  private final Trace<T> chain;

  public Trace(T elem) {
    this(elem, null);
  }

  public Trace(T elem, Trace<T> chain) {
    this.elem = requireNonNull(elem);
    this.chain = chain;
  }

  @Override
  public int hashCode() {
    return Objects.hash(elem, chain);
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Trace<?> trace
        && Objects.equals(this.elem, trace.elem)
        && Objects.equals(this.chain, trace.chain);
  }

  @Override
  public String toString() {
    var line = elem.toString();
    if (chain == null) {
      return line;
    } else {
      return line + "\n" + chain;
    }
  }
}
