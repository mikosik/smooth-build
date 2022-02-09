package org.smoothbuild.install;

import static org.smoothbuild.fs.base.PathS.path;
import static org.smoothbuild.fs.space.FilePath.filePath;
import static org.smoothbuild.fs.space.Space.PRJ;

import org.smoothbuild.fs.base.PathS;
import org.smoothbuild.fs.space.FilePath;

public class ProjectPaths {
  public static final PathS SMOOTH_DIR = path(".smooth");
  public static final PathS TEMPORARY_PATH = SMOOTH_DIR.appendPart("temporary");
  public static final PathS ARTIFACTS_PATH = SMOOTH_DIR.appendPart("artifacts");
  public static final PathS COMPUTATION_CACHE_PATH = SMOOTH_DIR.appendPart("computations");
  public static final PathS HASHED_DB_PATH = SMOOTH_DIR.appendPart("hashed");
  public static final PathS SMOOTH_LOCK_PATH = SMOOTH_DIR.appendPart("lock");
  public static final String PRJ_MOD_FILE_NAME = "build.smooth";
  public static final PathS PRJ_MOD_PATH = path(PRJ_MOD_FILE_NAME);
  public static final FilePath PRJ_MOD_FILE_PATH = filePath(PRJ, PRJ_MOD_PATH);
}
