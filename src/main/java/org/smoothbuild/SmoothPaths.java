package org.smoothbuild;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SmoothPaths {
  private static final String LIB_DIR_NAME = "lib";
  private static final String FUNCS_MODULE = "funcs.smooth";
  private static final String CONVERT_MODULE = "convert.smooth";

  private final Path home;

  public SmoothPaths(Path home) {
    this.home = home;
  }

  public Path convertModule() {
    return libDir().resolve(CONVERT_MODULE);
  }

  public Path funcsModule() {
    return libDir().resolve(FUNCS_MODULE);
  }

  public Path libDir() {
    return home.resolve(LIB_DIR_NAME);
  }

  public Path defaultScript() {
    return Paths.get("build.smooth");
  }
}
