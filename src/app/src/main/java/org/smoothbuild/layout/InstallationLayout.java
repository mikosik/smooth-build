package org.smoothbuild.layout;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.filesystem.base.PathS.path;
import static org.smoothbuild.common.filesystem.space.FilePath.filePath;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.common.filesystem.space.FilePath;

public class InstallationLayout {
  public static final PathS STANDARD_LIBRARY_MODULE_PATH = path("std_lib.smooth");
  public static final FilePath STANDARD_LIBRARY_MODULE_FILE_PATH =
      filePath(SmoothSpace.STANDARD_LIBRARY, STANDARD_LIBRARY_MODULE_PATH);
  public static final List<FilePath> STANDARD_LIBRARY_MODULES =
      list(STANDARD_LIBRARY_MODULE_FILE_PATH);
  public static final FilePath SMOOTH_JAR_FILE_PATH =
      filePath(SmoothSpace.BINARY, path("smooth.jar"));
  public static final String STANDARD_LIBRARY_DIR_NAME = "lib";
  public static final String BIN_DIR_NAME = "bin";
}
