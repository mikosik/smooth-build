package org.smoothbuild.install;

import static org.smoothbuild.lang.base.Space.STANDARD_LIBRARY;

import java.nio.file.Path;
import java.util.List;

import org.smoothbuild.lang.base.ModulePath;

public class InstallationPaths {
  private static final String LIB_DIR_NAME = "lib";
  private static final String SLIB_MODULE_FILE = "slib.smooth";
  private static final String SMOOTH_JAR = "smooth.jar";
  private final Path installationDir;

  public InstallationPaths(Path installationDir) {
    this.installationDir = installationDir;
  }

  public List<ModulePath> slibModules() {
    String file = SLIB_MODULE_FILE;
    return List.of(new ModulePath(STANDARD_LIBRARY, libDir().resolve(file), "{slib}/" + file));
  }

  private Path libDir() {
    return installationDir.resolve(LIB_DIR_NAME);
  }

  public Path smoothJar() {
    return installationDir.resolve(SMOOTH_JAR);
  }
}
