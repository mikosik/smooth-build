package org.smoothbuild.compile.fs.lang.base.location;

import static org.smoothbuild.fs.space.Space.PROJECT;

/**
 * Location.
 * This class is immutable.
 */
public sealed interface Location
    permits InternalLocation, SourceLocation, UnknownLocation {
  public default boolean isInProjectSpace() {
    return (this instanceof SourceLocation source) && source.space().equals(PROJECT);
  }

  public default String description() {
    return toString();
  }
}
