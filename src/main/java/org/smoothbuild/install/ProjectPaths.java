package org.smoothbuild.install;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.base.define.FilePath.filePath;
import static org.smoothbuild.lang.base.define.Space.PRJ;

import java.nio.file.Paths;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.define.FilePath;

public class ProjectPaths {
  public static final Path SMOOTH_DIR = path(".smooth");
  public static final Path TEMPORARY_PATH = SMOOTH_DIR.appendPart("temporary");
  public static final Path ARTIFACTS_PATH = SMOOTH_DIR.appendPart("artifacts");
  public static final Path COMPUTATION_CACHE_PATH = SMOOTH_DIR.appendPart("computations");
  public static final Path OBJECT_DB_PATH = SMOOTH_DIR.appendPart("objects");
  public static final Path SMOOTH_LOCK_PATH = SMOOTH_DIR.appendPart("lock");
  public static final String PRJ_MODULE_FILE_NAME = "build.smooth";
  public static final Path PRJ_MODULE_PATH = path(PRJ_MODULE_FILE_NAME);
  public static final FilePath PRJ_MODULE_LOCATION =
      filePath(PRJ, Paths.get(PRJ_MODULE_PATH.toString()));
}
