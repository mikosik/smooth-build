package org.smoothbuild.compile.fs.lang.base.location;

import org.smoothbuild.fs.space.Space;

public sealed interface SourceLocation
    extends Location
    permits CommandLineLocation, FileLocation {
  public Space space();
  public int line();
}
