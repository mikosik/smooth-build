package org.smoothbuild.compile.frontend.lang.base.location;

/**
 * Location.
 * This class is immutable.
 */
public sealed interface Location permits InternalLocation, SourceLocation, UnknownLocation {
  public default String description() {
    return toString();
  }
}
