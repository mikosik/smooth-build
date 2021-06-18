package org.smoothbuild.lang.base.define;

import static org.smoothbuild.io.fs.base.TestingFilePath.importedFilePath;
import static org.smoothbuild.io.fs.base.TestingFilePath.smoothFilePath;

import java.util.Optional;

import org.smoothbuild.io.fs.base.FilePath;

public class TestingModuleFiles {
  public static ModuleFiles moduleFiles() {
    return moduleFiles(smoothFilePath());
  }

  public static ModuleFiles importedModuleFiles() {
    return moduleFiles(importedFilePath());
  }

  private static ModuleFiles moduleFiles(FilePath filePath) {
    return new ModuleFiles(filePath, Optional.empty());
  }
}
