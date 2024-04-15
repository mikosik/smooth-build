package org.smoothbuild.app.layout;

import static org.smoothbuild.app.layout.BucketIds.INSTALL;
import static org.smoothbuild.app.layout.BucketIds.LIBRARY;
import static org.smoothbuild.app.layout.BucketIds.PROJECT;
import static org.smoothbuild.common.bucket.base.FullPath.fullPath;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;

import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.common.collect.List;

public class Layout {
  public static final Path SMOOTH_DIR = path(".smooth");
  public static final Path ARTIFACTS_PATH = SMOOTH_DIR.appendPart("artifacts");
  public static final Path COMPUTATION_CACHE_PATH = SMOOTH_DIR.appendPart("computations");
  public static final Path HASHED_DB_PATH = SMOOTH_DIR.appendPart("hashed");
  public static final Path SMOOTH_LOCK_PATH = SMOOTH_DIR.appendPart("lock");
  public static final Path DEFAULT_MODULE_PATH = path("build.smooth");
  public static final FullPath DEFAULT_MODULE_FILE_PATH = fullPath(PROJECT, DEFAULT_MODULE_PATH);
  public static final Path STANDARD_LIBRARY_MODULE_PATH = path("std_lib.smooth");
  public static final FullPath STANDARD_LIBRARY_MODULE_FILE_PATH =
      fullPath(LIBRARY, STANDARD_LIBRARY_MODULE_PATH);
  public static final List<FullPath> STANDARD_LIBRARY_MODULES =
      list(STANDARD_LIBRARY_MODULE_FILE_PATH);
  public static final List<FullPath> MODULES =
      listOfAll(STANDARD_LIBRARY_MODULES).append(DEFAULT_MODULE_FILE_PATH);
  public static final FullPath SMOOTH_JAR_FILE_PATH = fullPath(INSTALL, path("smooth.jar"));
  public static final String STANDARD_LIBRARY_DIR_NAME = "lib";
  public static final String BIN_DIR_NAME = "bin";
}
