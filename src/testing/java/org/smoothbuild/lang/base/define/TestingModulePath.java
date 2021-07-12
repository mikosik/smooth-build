package org.smoothbuild.lang.base.define;

import static org.smoothbuild.io.fs.base.TestingFilePath.importedFilePath;
import static org.smoothbuild.io.fs.base.TestingFilePath.smoothFilePath;

public class TestingModulePath {
  public static ModulePath modulePath() {
    return ModulePath.of(smoothFilePath());
  }

  public static ModulePath importedModulePath() {
    return ModulePath.of(importedFilePath());
  }
}
