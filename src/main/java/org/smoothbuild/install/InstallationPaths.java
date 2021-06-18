package org.smoothbuild.install;

import static org.smoothbuild.io.fs.base.FilePath.filePath;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.Space.SDK;
import static org.smoothbuild.util.Lists.list;

import java.nio.file.Path;

import org.smoothbuild.io.fs.base.FilePath;

import com.google.common.collect.ImmutableList;

public class InstallationPaths {
  private static final String LIB_DIR_NAME = "lib";
  public static final ImmutableList<FilePath> SDK_MODULES = list(filePath(SDK, path("api.smooth")));
  private static final String SMOOTH_JAR = "smooth.jar";
  private final java.nio.file.Path installationDir;

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
