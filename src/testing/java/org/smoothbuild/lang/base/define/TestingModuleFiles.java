package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.Space.USER;
import static org.smoothbuild.lang.base.define.TestingFileLocation.importedFileLocation;
import static org.smoothbuild.lang.base.define.TestingFileLocation.smoothFileLocation;

import java.util.Optional;

public class TestingModuleFiles {
  public static ModuleFiles moduleFiles() {
    return moduleFiles(smoothFileLocation());
  }

  public static ModuleFiles importedModuleFiles() {
    return moduleFiles(importedFileLocation());
  }

  private static ModuleFiles moduleFiles(FileLocation fileLocation) {
    return new ModuleFiles(ModulePath.of(fileLocation), USER, fileLocation, Optional.empty());
  }
}
