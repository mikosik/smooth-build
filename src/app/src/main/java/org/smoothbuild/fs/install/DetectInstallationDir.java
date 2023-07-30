package org.smoothbuild.fs.install;

import java.net.URISyntaxException;
import java.nio.file.Path;

public class DetectInstallationDir {
  public static Path detectInstallationDir() {
    return smoothJarPath().getParent();
  }

  private static Path smoothJarPath() {
    try {
      var uri = DetectInstallationDir.class.getProtectionDomain()
          .getCodeSource()
          .getLocation()
          .toURI();
      return Path.of(uri).getParent();
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
}
