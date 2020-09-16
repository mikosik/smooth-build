package org.smoothbuild.lang.base;

import static org.smoothbuild.lang.base.Space.USER;

import java.nio.file.Path;

public class TestingModuleLocation {
  public static final Path BUILD_FILE_PATH = Path.of("myBuild.smooth");

  public static ModuleLocation moduleLocation() {
    return ModuleLocation.moduleLocation(USER, BUILD_FILE_PATH);
  }

  public static ModuleLocation importedModuleLocation() {
    return ModuleLocation.moduleLocation(USER, Path.of("imported.smooth"));
  }
}
