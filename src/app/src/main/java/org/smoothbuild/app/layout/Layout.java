package org.smoothbuild.app.layout;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.filesystem.base.FullPath.fullPath;
import static org.smoothbuild.common.filesystem.base.Path.path;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.Path;

public class Layout {
  public static final Path SMOOTH_DIR = path(".smooth");
  public static final Path ARTIFACTS_PATH = SMOOTH_DIR.appendPart("artifacts");
  public static final Path COMPUTATION_CACHE_PATH = SMOOTH_DIR.appendPart("computations");
  public static final Path HASHED_DB_PATH = SMOOTH_DIR.appendPart("hashed");
  public static final Path SMOOTH_LOCK_PATH = SMOOTH_DIR.appendPart("lock");
  public static final Path DEFAULT_MODULE_PATH = path("build.smooth");
  public static final FullPath DEFAULT_MODULE_FILE_PATH =
      fullPath(SmoothSpace.PROJECT, DEFAULT_MODULE_PATH);
  public static final Path STANDARD_LIBRARY_MODULE_PATH = path("std_lib.smooth");
  public static final FullPath STANDARD_LIBRARY_MODULE_FILE_PATH =
      fullPath(SmoothSpace.STANDARD_LIBRARY, STANDARD_LIBRARY_MODULE_PATH);
  public static final List<FullPath> STANDARD_LIBRARY_MODULES =
      list(STANDARD_LIBRARY_MODULE_FILE_PATH);
  public static final List<FullPath> MODULES =
      listOfAll(STANDARD_LIBRARY_MODULES).append(DEFAULT_MODULE_FILE_PATH);
  public static final FullPath SMOOTH_JAR_FILE_PATH =
      fullPath(SmoothSpace.BINARY, path("smooth.jar"));
  public static final String STANDARD_LIBRARY_DIR_NAME = "lib";
  public static final String BIN_DIR_NAME = "bin";
}