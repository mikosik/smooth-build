package org.smoothbuild.common.log.location;

/**
 * Location.
 * This class is immutable.
 */
public sealed interface Location permits InternalLocation, SourceLocation, UnknownLocation {
  public default String description() {
    return toString();
  }
}
