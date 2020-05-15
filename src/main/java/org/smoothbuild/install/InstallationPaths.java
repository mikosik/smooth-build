package org.smoothbuild.install;

import static org.smoothbuild.SmoothConstants.USER_MODULE;
import static org.smoothbuild.lang.base.Space.STANDARD_LIBRARY;
import static org.smoothbuild.util.Lists.concat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Singleton;

import org.smoothbuild.lang.base.ModulePath;
import org.smoothbuild.util.reflect.Classes;

@Singleton
public class InstallationPaths {
  private static final String LIB_DIR_NAME = "lib";
  private static final String SLIB_MODULE_FILE = "slib.smooth";
  private final Path installationDir;

  public static InstallationPaths installationPaths() {
    return new InstallationPaths(smoothInstallationDir());
  }

  private InstallationPaths(Path installationDir) {
    this.installationDir = installationDir;
  }

  public List<ModulePath> allModules() {
    return concat(slibModules(), USER_MODULE);
  }

  public List<ModulePath> slibModules() {
    String file = SLIB_MODULE_FILE;
    return List.of(new ModulePath(STANDARD_LIBRARY, libDir().resolve(file), "{slib}/" + file));
  }

  private Path libDir() {
    return installationDir.resolve(LIB_DIR_NAME);
  }

  public static Path smoothInstallationDir() {
    return smoothJarPath().getParent();
  }

  public static Path smoothJarPath() {
    String resourcePath = InstallationPaths.class
        .getClassLoader()
        .getResource(Classes.binaryPath(InstallationPaths.class))
        .getPath();
    String smoothJarPath = resourcePath
        .substring(0, resourcePath.lastIndexOf('!'))
        .substring("file:".length());
    return Paths.get(smoothJarPath);
  }
}
