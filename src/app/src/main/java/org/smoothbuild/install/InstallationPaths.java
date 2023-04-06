package org.smoothbuild.install;

import static org.smoothbuild.fs.base.PathS.path;
import static org.smoothbuild.fs.space.FilePath.filePath;
import static org.smoothbuild.fs.space.Space.STD_LIB;
import static org.smoothbuild.util.collect.Lists.list;

import java.nio.file.Path;

import org.smoothbuild.fs.base.PathS;
import org.smoothbuild.fs.space.FilePath;

import com.google.common.collect.ImmutableList;

public class InstallationPaths {
  private static final String SLIB_DIR_NAME = "lib";
  public static final PathS SLIB_MOD_PATH = path("std_lib.smooth");
  public static final FilePath SLIB_MOD_FILE_PATH = filePath(STD_LIB, SLIB_MOD_PATH);
  public static final ImmutableList<FilePath> SLIB_MODS = list(SLIB_MOD_FILE_PATH);
  private static final String SMOOTH_JAR = "smooth.jar";
  private final Path installationDir;

  public InstallationPaths(Path installationDir) {
    this.installationDir = installationDir;
  }

  public Path standardLibraryDir() {
    return installationDir.resolve(SLIB_DIR_NAME);
  }

  public Path smoothJar() {
    return installationDir.resolve(SMOOTH_JAR);
  }
}
