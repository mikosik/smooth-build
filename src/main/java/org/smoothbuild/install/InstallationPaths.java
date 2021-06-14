package org.smoothbuild.install;

import static org.smoothbuild.lang.base.define.Space.STANDARD_LIBRARY;
import static org.smoothbuild.util.Lists.list;

import java.nio.file.Path;

import org.smoothbuild.lang.base.define.SModule;

import com.google.common.collect.ImmutableList;

public class InstallationPaths {
  private static final String LIB_DIR_NAME = "lib";
  private static final Path SLIB_MODULE_FILE = Path.of("slib.smooth");
  public static final ImmutableList<SModule>
      STANDARD_LIBRARY_MODULES = list(new SModule(STANDARD_LIBRARY, SLIB_MODULE_FILE, list()));
  private static final String SMOOTH_JAR = "smooth.jar";
  private final Path installationDir;

  public InstallationPaths(Path installationDir) {
    this.installationDir = installationDir;
  }

  public Path standardLibraryDir() {
    return installationDir.resolve(LIB_DIR_NAME);
  }

  public Path smoothJar() {
    return installationDir.resolve(SMOOTH_JAR);
  }
}
