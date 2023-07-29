package org.smoothbuild.fs.install;

import static org.smoothbuild.fs.space.FilePath.filePath;
import static org.smoothbuild.fs.space.Space.STD_LIB;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.fs.base.PathS.path;

import java.nio.file.Path;

import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.util.fs.base.PathS;

import com.google.common.collect.ImmutableList;

public class InstallationPaths {
  public static final String STD_LIB_DIR_NAME = "lib";
  public static final PathS STD_LIB_MOD_PATH = path("std_lib.smooth");
  public static final FilePath STD_LIB_MOD_FILE_PATH = filePath(STD_LIB, STD_LIB_MOD_PATH);
  public static final ImmutableList<FilePath> STD_LIB_MODS = list(STD_LIB_MOD_FILE_PATH);
  private static final String SMOOTH_JAR = "smooth.jar";
  private final Path installationDir;

  public InstallationPaths(Path installationDir) {
    this.installationDir = installationDir;
  }

  public Path smoothJar() {
    return installationDir.resolve(SMOOTH_JAR);
  }
}
