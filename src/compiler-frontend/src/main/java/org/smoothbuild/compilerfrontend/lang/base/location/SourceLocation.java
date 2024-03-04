package org.smoothbuild.compilerfrontend.lang.base.location;

import org.smoothbuild.common.filesystem.base.Space;

public sealed interface SourceLocation extends Location permits CommandLineLocation, FileLocation {
  public Space space();

  public int line();
}
