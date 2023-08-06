package org.smoothbuild.layout;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.filesystem.base.PathS.path;
import static org.smoothbuild.common.filesystem.space.FilePath.filePath;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.common.filesystem.space.FilePath;

public class Layout {
  public static final PathS SMOOTH_DIR = path(".smooth");
  public static final PathS ARTIFACTS_PATH = SMOOTH_DIR.appendPart("artifacts");
  public static final PathS COMPUTATION_CACHE_PATH = SMOOTH_DIR.appendPart("computations");
  public static final PathS HASHED_DB_PATH = SMOOTH_DIR.appendPart("hashed");
  public static final PathS SMOOTH_LOCK_PATH = SMOOTH_DIR.appendPart("lock");
  public static final PathS DEFAULT_MODULE_PATH = path("build.smooth");
  public static final FilePath DEFAULT_MODULE_FILE_PATH =
      filePath(SmoothSpace.PROJECT, DEFAULT_MODULE_PATH);
  public static final PathS STANDARD_LIBRARY_MODULE_PATH = path("std_lib.smooth");
  public static final FilePath STANDARD_LIBRARY_MODULE_FILE_PATH =
      filePath(SmoothSpace.STANDARD_LIBRARY, STANDARD_LIBRARY_MODULE_PATH);
  public static final List<FilePath> STANDARD_LIBRARY_MODULES =
      list(STANDARD_LIBRARY_MODULE_FILE_PATH);
  public static final List<FilePath> MODULES =
      listOfAll(STANDARD_LIBRARY_MODULES).append(DEFAULT_MODULE_FILE_PATH);
  public static final FilePath SMOOTH_JAR_FILE_PATH =
      filePath(SmoothSpace.BINARY, path("smooth.jar"));
  public static final String STANDARD_LIBRARY_DIR_NAME = "lib";
  public static final String BIN_DIR_NAME = "bin";
}
