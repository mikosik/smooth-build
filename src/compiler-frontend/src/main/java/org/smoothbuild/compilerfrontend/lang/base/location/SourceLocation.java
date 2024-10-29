package org.smoothbuild.compilerfrontend.lang.base.location;

public sealed interface SourceLocation extends Location permits CommandLineLocation, FileLocation {
  public int line();
}
