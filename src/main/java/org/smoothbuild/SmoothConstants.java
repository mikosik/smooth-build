package org.smoothbuild;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.base.Space.USER;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.ModulePath;

public class SmoothConstants {
  public static final int EXIT_CODE_SUCCESS = 0;
  public static final int EXIT_CODE_JAVA_EXCEPTION = 1;
  public static final int EXIT_CODE_ERROR = 2;

  public static final Charset CHARSET = StandardCharsets.UTF_8;

  public static final Path SMOOTH_DIR = path(".smooth");
  public static final Path SMOOTH_LOCK_PATH = SMOOTH_DIR.appendPart("lock");
  public static final Path HASHED_DB_PATH = SMOOTH_DIR.appendPart("hashed");
  public static final Path OUTPUTS_DB_PATH = SMOOTH_DIR.appendPart("outputs");
  public static final Path ARTIFACTS_PATH = SMOOTH_DIR.appendPart("artifacts");
  public static final Path TEMPORARY_PATH = SMOOTH_DIR.appendPart("temporary");
  public static final Path USER_MODULE_FILE = path("build.smooth");
  public static final ModulePath USER_MODULE =
      new ModulePath(USER, USER_MODULE_FILE.toJPath(), USER_MODULE_FILE.toString());

}
