package org.smoothbuild;

import static org.smoothbuild.io.fs.base.Path.path;

import java.nio.charset.Charset;

import org.smoothbuild.io.fs.base.Path;

import com.google.common.base.Charsets;

public class SmoothContants {
  public static final Path DEFAULT_SCRIPT = path("build.smooth");
  public static final Charset CHARSET = Charsets.UTF_8;

  public static final Path SMOOTH_DIR = path(".smooth");
  public static final Path OBJECTS_DIR = path("values");
  public static final Path TASK_RESULTS_DIR = path("tasks");
  public static final Path ARTIFACTS_DIR = path("results");
}
