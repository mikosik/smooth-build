package org.smoothbuild.lang.base.define;

import static org.smoothbuild.io.fs.base.TestingFilePath.filePath;

public class TestingLoc {
  public static Loc loc() {
    return loc(11);
  }

  public static Loc loc(int line) {
    return Loc.loc(filePath(), line);
  }
}
