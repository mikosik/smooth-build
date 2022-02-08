package org.smoothbuild.install;

import static org.smoothbuild.io.fs.base.PathS.path;
import static org.smoothbuild.io.fs.space.FilePath.filePath;
import static org.smoothbuild.io.fs.space.Space.SDK;
import static org.smoothbuild.util.collect.Lists.list;

import java.nio.file.Path;

import org.smoothbuild.io.fs.base.PathS;
import org.smoothbuild.io.fs.space.FilePath;

import com.google.common.collect.ImmutableList;

public class InstallationPaths {
  public static final String LIB_DIR_NAME = "lib";
  private static final PathS API_MOD_PATH = path("api.smooth");
  public static final FilePath API_MOD_FILE_PATH = filePath(SDK, API_MOD_PATH);
  public static final ImmutableList<FilePath> SDK_MODS = list(API_MOD_FILE_PATH);
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
