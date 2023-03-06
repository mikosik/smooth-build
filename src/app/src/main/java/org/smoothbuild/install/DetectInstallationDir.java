package org.smoothbuild.install;

import java.nio.file.Path;

import org.smoothbuild.util.reflect.Classes;

public class DetectInstallationDir {
  public static Path detectInstallationDir() {
    return smoothJarPath().getParent();
  }

  public static Path smoothJarPath() {
    String resourcePath = DetectInstallationDir.class
        .getClassLoader()
        .getResource(Classes.binaryPath(DetectInstallationDir.class))
        .getPath();
    String smoothJarPath = resourcePath
        .substring(0, resourcePath.lastIndexOf('!'))
        .substring("file:".length());
    return Path.of(smoothJarPath);
  }
}
