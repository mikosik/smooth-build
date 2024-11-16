package org.smoothbuild.common.log.location;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.filesystem.base.FullPath;

public class Locations {
  public static Location commandLineLocation() {
    return CommandLineLocation.INSTANCE;
  }

  public static Location internalLocation() {
    return InternalLocation.INSTANCE;
  }

  public static Location unknownLocation() {
    return UnknownLocation.INSTANCE;
  }

  public static FileLocation fileLocation(FullPath fullPath, int line) {
    checkArgument(0 < line);
    return new FileLocation(fullPath, line);
  }
}
