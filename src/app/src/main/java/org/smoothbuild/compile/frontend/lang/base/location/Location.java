package org.smoothbuild.compile.frontend.lang.base.location;

import static org.smoothbuild.layout.SmoothSpace.PROJECT;

/**
 * Location.
 * This class is immutable.
 */
public sealed interface Location permits InternalLocation, SourceLocation, UnknownLocation {
  public default boolean isInProjectSpace() {
    return (this instanceof SourceLocation source) && source.space().equals(PROJECT);
  }

  public default String description() {
    return toString();
  }
}
