package org.smoothbuild.command;

import static org.smoothbuild.fs.base.Path.path;

import org.smoothbuild.fs.base.Path;

public class SmoothContants {
  public static final Path DEFAULT_SCRIPT = path("build.smooth");
  public static final Path BUILD_DIR = path(".smooth");
  public static final Path VALUE_DB_DIR = BUILD_DIR.append(path("values"));
  public static final Path TASK_RESULT_DIR = BUILD_DIR.append(path("cache"));
}
