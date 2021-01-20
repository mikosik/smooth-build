package org.smoothbuild.install;

import static org.smoothbuild.lang.base.define.ModuleLocation.moduleLocation;
import static org.smoothbuild.lang.base.define.Space.STANDARD_LIBRARY;

import java.nio.file.Path;
import java.util.List;

import org.smoothbuild.lang.base.define.ModuleLocation;

public class InstallationPaths {
  private static final String LIB_DIR_NAME = "lib";
  private static final Path SLIB_MODULE_FILE = Path.of("slib.smooth");
  private static final String SMOOTH_JAR = "smooth.jar";
  private final Path installationDir;

  public InstallationPaths(Path installationDir) {
    this.installationDir = installationDir;
  }

  public static List<ModuleLocation> standardLibraryModuleLocations() {
    return List.of(moduleLocation(STANDARD_LIBRARY, SLIB_MODULE_FILE));
  }

  public Path standardLibraryDir() {
    return installationDir.resolve(LIB_DIR_NAME);
  }

  public Path smoothJar() {
    return installationDir.resolve(SMOOTH_JAR);
  }
}
