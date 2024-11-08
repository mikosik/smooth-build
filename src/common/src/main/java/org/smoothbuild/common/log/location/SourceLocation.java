package org.smoothbuild.common.log.location;

public sealed interface SourceLocation extends Location permits CommandLineLocation, FileLocation {
  public int line();
}
