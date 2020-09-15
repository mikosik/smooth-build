package org.smoothbuild.install;

import static org.smoothbuild.io.fs.base.Path.path;

import org.smoothbuild.io.fs.base.Path;

public class ProjectPaths {
  public static final Path SMOOTH_DIR = path(".smooth");
  public static final Path TEMPORARY_PATH = SMOOTH_DIR.appendPart("temporary");
  public static final Path ARTIFACTS_PATH = SMOOTH_DIR.appendPart("artifacts");
  public static final Path COMPUTATION_CACHE_PATH = SMOOTH_DIR.appendPart("computations");
  public static final Path OBJECT_DB_PATH = SMOOTH_DIR.appendPart("objects");
  public static final Path SMOOTH_LOCK_PATH = SMOOTH_DIR.appendPart("lock");
  public static final String USER_MODULE_FILE_NAME = "build.smooth";
  public static final Path USER_MODULE_PATH = path(USER_MODULE_FILE_NAME);
}
