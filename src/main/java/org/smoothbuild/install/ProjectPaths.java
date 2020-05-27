package org.smoothbuild.install;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.base.Space.USER;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.ModulePath;

public class ProjectPaths {
  public static final Path SMOOTH_DIR = path(".smooth");
  public static final Path TEMPORARY_PATH = SMOOTH_DIR.appendPart("temporary");
  public static final Path ARTIFACTS_PATH = SMOOTH_DIR.appendPart("artifacts");
  public static final Path OUTPUTS_DB_PATH = SMOOTH_DIR.appendPart("outputs");
  public static final Path HASHED_DB_PATH = SMOOTH_DIR.appendPart("hashed");
  public static final Path SMOOTH_LOCK_PATH = SMOOTH_DIR.appendPart("lock");
  public static final String USER_MODULE_FILE_NAME = "build.smooth";
  public static final Path USER_MODULE_PATH = path(USER_MODULE_FILE_NAME);

  private final java.nio.file.Path projectDir;

  public ProjectPaths(java.nio.file.Path projectDir) {
    this.projectDir = projectDir;
  }

  public ModulePath userModule() {
    return new ModulePath(
        USER,
        projectDir.resolve(USER_MODULE_PATH.toString()),
        USER_MODULE_PATH.toString());
  }
}
