package org.smoothbuild;

import static org.smoothbuild.lang.base.Space.STANDARD_LIBRARY;
import static org.smoothbuild.lang.base.Space.USER;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Singleton;

import org.smoothbuild.lang.base.ModulePath;
import org.smoothbuild.util.reflect.Classes;

@Singleton
public class SmoothPaths {
  private static final String LIB_DIR_NAME = "lib";
  private static final String FUNCS_MODULE_FILE = "funcs.smooth";
  private static final String USER_MODULE_FILE = "build.smooth";
  public static final ModulePath USER_MODULE =
      new ModulePath(USER, Paths.get(USER_MODULE_FILE), USER_MODULE_FILE);

  private final Path installationDir;

  public static SmoothPaths smoothPaths() {
    return new SmoothPaths(smoothInstallationDir());
  }

  private SmoothPaths(Path installationDir) {
    this.installationDir = installationDir;
  }

  public ModulePath funcsModule() {
    return new ModulePath(
        STANDARD_LIBRARY, libDir().resolve(FUNCS_MODULE_FILE), "{SL}/" + FUNCS_MODULE_FILE);
  }

  private Path libDir() {
    return installationDir.resolve(LIB_DIR_NAME);
  }

  public ModulePath userModule() {
    return USER_MODULE;
  }

  public static Path smoothInstallationDir() {
    return smoothJarPath().getParent();
  }

  public static Path smoothJarPath() {
    String resourcePath = SmoothPaths.class
        .getClassLoader()
        .getResource(Classes.binaryPath(SmoothPaths.class))
        .getPath();
    String smoothJarPath = resourcePath
        .substring(0, resourcePath.lastIndexOf('!'))
        .substring("file:".length());
    return Paths.get(smoothJarPath);
  }
}
