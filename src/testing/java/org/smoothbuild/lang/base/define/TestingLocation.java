package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.Location.location;
import static org.smoothbuild.lang.base.define.TestingFileLocation.fileLocation;

public class TestingLocation {
  public static Location loc() {
    return loc(11);
  }

  public static Location loc(int line) {
    return location(fileLocation(), line);
  }
}
