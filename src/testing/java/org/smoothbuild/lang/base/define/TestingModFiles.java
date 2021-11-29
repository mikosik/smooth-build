package org.smoothbuild.lang.base.define;

import static org.smoothbuild.io.fs.base.TestingFilePath.importedFilePath;
import static org.smoothbuild.io.fs.base.TestingFilePath.smoothFilePath;

import java.util.Optional;

import org.smoothbuild.io.fs.space.FilePath;

public class TestingModFiles {
  public static ModFiles modFiles() {
    return modFiles(smoothFilePath());
  }

  public static ModFiles importedModFiles() {
    return modFiles(importedFilePath());
  }

  private static ModFiles modFiles(FilePath filePath) {
    return new ModFiles(filePath, Optional.empty());
  }
}
