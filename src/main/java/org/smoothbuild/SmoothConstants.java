package org.smoothbuild;

import static org.smoothbuild.io.fs.base.Path.path;

import java.nio.charset.Charset;

import org.smoothbuild.io.fs.base.Path;

import com.google.common.base.Charsets;

public class SmoothConstants {
  public static final int EXIT_CODE_SUCCESS = 0;
  public static final int EXIT_CODE_ERROR = 2;
  public static final String SMOOTH_HOME_ENV_VARIABLE = "SMOOTH_HOME";
  public static final String SMOOTH_HOME_LIB_DIR = "lib";

  public static final Path DEFAULT_SCRIPT = path("build.smooth");
  public static final Charset CHARSET = Charsets.UTF_8;

  public static final Path SMOOTH_DIR = path(".smooth");
  public static final Path VALUES_DIR = path("values");
  public static final Path VALUES_DB_PATH = SMOOTH_DIR.append(VALUES_DIR);
  public static final Path OUTPUTS_DIR = path("outputs");
  public static final Path OUTPUTS_DB_PATH = SMOOTH_DIR.append(OUTPUTS_DIR);
  public static final Path ARTIFACTS_DIR = path("artifacts");
  public static final Path TEMPORARY_DIR = path("temporary");
  public static final Path TEMPORARY_PATH = SMOOTH_DIR.append(TEMPORARY_DIR);
}
