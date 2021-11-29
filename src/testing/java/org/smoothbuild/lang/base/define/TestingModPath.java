package org.smoothbuild.lang.base.define;

import static org.smoothbuild.io.fs.base.TestingFilePath.importedFilePath;
import static org.smoothbuild.io.fs.base.TestingFilePath.smoothFilePath;

public class TestingModPath {
  public static ModPath modPath() {
    return ModPath.of(smoothFilePath());
  }

  public static ModPath importedModPath() {
    return ModPath.of(importedFilePath());
  }
}
