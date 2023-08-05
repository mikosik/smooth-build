package org.smoothbuild.compile.fs.lang.base.location;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.filesystem.space.FilePath;

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

  public static FileLocation fileLocation(FilePath filePath, int line) {
    checkArgument(0 < line);
    return new FileLocation(filePath, line);
  }
}
