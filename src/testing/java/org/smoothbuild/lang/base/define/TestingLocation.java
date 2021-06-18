package org.smoothbuild.lang.base.define;

import static org.smoothbuild.io.fs.base.TestingFilePath.filePath;
import static org.smoothbuild.lang.base.define.Location.location;

public class TestingLocation {
  public static Location loc() {
    return loc(11);
  }

  public static Location loc(int line) {
    return location(filePath(), line);
  }
}
