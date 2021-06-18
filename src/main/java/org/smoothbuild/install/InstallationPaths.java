package org.smoothbuild.install;

import static org.smoothbuild.lang.base.define.Space.SDK;
import static org.smoothbuild.util.Lists.list;

import java.nio.file.Path;

import org.smoothbuild.lang.base.define.FileLocation;

import com.google.common.collect.ImmutableList;

public class InstallationPaths {
  private static final String LIB_DIR_NAME = "lib";
  private static final Path SDK_API_MODULE_FILE = Path.of("api.smooth");
  public static final ImmutableList<FileLocation>
      SDK_MODULES = list(new FileLocation(SDK, SDK_API_MODULE_FILE));
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
