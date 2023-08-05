package org.smoothbuild.fs.install;

import static org.smoothbuild.common.collect.Lists.list;
import static org.smoothbuild.common.fs.base.PathS.path;
import static org.smoothbuild.fs.space.FilePath.filePath;
import static org.smoothbuild.fs.space.Space.STANDARD_LIBRARY;

import org.smoothbuild.common.fs.base.PathS;
import org.smoothbuild.fs.space.FilePath;

import com.google.common.collect.ImmutableList;

public class InstallationPaths {
  public static final PathS STD_LIB_MOD_PATH = path("std_lib.smooth");
  public static final FilePath STD_LIB_MOD_FILE_PATH = filePath(STANDARD_LIBRARY, STD_LIB_MOD_PATH);
  public static final ImmutableList<FilePath> STD_LIB_MODS = list(STD_LIB_MOD_FILE_PATH);
  public static final PathS SMOOTH_JAR = path("smooth.jar");
  public static final String STD_LIB_DIR_NAME = "lib";
  public static final String BIN_DIR_NAME = "bin";
}
