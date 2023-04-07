package org.smoothbuild.install;

import java.net.URISyntaxException;
import java.nio.file.Path;

public class DetectInstallationDir {
  public static Path detectInstallationDir() {
    return smoothJarPath().getParent();
  }

  public static Path smoothJarPath() {
    try {
      var path = DetectInstallationDir.class.getProtectionDomain()
          .getCodeSource()
          .getLocation()
          .toURI()
          .getPath();
      return Path.of(path);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
}
