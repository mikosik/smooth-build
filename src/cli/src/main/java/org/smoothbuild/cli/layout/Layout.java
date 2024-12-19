package org.smoothbuild.cli.layout;

import static org.smoothbuild.cli.layout.Aliases.INSTALL_ALIAS;
import static org.smoothbuild.cli.layout.Aliases.LIBRARY_ALIAS;
import static org.smoothbuild.cli.layout.Aliases.PROJECT_ALIAS;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.filesystem.base.FullPath.fullPath;
import static org.smoothbuild.common.filesystem.base.Path.path;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.Path;

public class Layout {
  public static final Path SMOOTH_DIR = path(".smooth");
  public static final FullPath PROJECT_PATH = PROJECT_ALIAS.append(Path.root());
  public static final FullPath SMOOTH_PATH = PROJECT_PATH.append(SMOOTH_DIR);
  public static final FullPath ARTIFACTS_PATH = SMOOTH_PATH.appendPart("artifacts");
  public static final FullPath COMPUTATION_DB_PATH = SMOOTH_PATH.appendPart("computations");
  public static final FullPath BYTECODE_DB_PATH = SMOOTH_PATH.appendPart("bytecode");
  public static final Path SMOOTH_LOCK_PATH = SMOOTH_DIR.appendPart("lock");
  public static final Path DEFAULT_MODULE_PATH = path("build.smooth");
  public static final FullPath DEFAULT_MODULE_FILE_PATH = PROJECT_ALIAS.append(DEFAULT_MODULE_PATH);
  public static final FullPath STD_LIB_MODULE_PATH = LIBRARY_ALIAS.append(path("std_lib.smooth"));
  public static final List<FullPath> STD_LIB_MODULES = list(STD_LIB_MODULE_PATH);
  public static final List<FullPath> MODULES = STD_LIB_MODULES.add(DEFAULT_MODULE_FILE_PATH);
  public static final FullPath SMOOTH_JAR_FILE_PATH = fullPath(INSTALL_ALIAS, path("smooth.jar"));
  public static final String STANDARD_LIBRARY_DIR_NAME = "lib";
  public static final String BIN_DIR_NAME = "bin";
}
