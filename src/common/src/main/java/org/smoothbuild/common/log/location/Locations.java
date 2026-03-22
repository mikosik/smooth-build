package org.smoothbuild.common.log.location;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.filesystem.base.FullPath;

public class Locations {
  public static Location commandLineLocation() {
    return new Location(CliArgumentSource.INSTANCE, 1);
  }

  public static Location internalLocation() {
    return new Location(BuiltinSource.INSTANCE, 0);
  }

  public static Location unknownLocation() {
    return new Location(UnknownSource.INSTANCE, 0);
  }

  public static Location fileLocation(FullPath fullPath, int line) {
    checkArgument(0 < line);
    return new Location(new FileSource(fullPath), line);
  }
}
