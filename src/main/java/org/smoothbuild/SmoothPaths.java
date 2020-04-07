package org.smoothbuild;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SmoothPaths {
  private static final String LIB_DIR_NAME = "lib";
  private static final String FUNCS_MODULE_FILE = "funcs.smooth";
  private static final String USER_MODULE_FILE = "build.smooth";
  private static final ModulePath USER_MODULE =
      new ModulePath(Paths.get(USER_MODULE_FILE), USER_MODULE_FILE);

  private final Path home;

  public SmoothPaths(Path home) {
    this.home = home;
  }

  public ModulePath funcsModule() {
    return new ModulePath(libDir().resolve(FUNCS_MODULE_FILE), "{SL}/" + FUNCS_MODULE_FILE);
  }

  private Path libDir() {
    return home.resolve(LIB_DIR_NAME);
  }

  public ModulePath userModule() {
    return USER_MODULE;
  }
}
