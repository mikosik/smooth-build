package org.smoothbuild.exec;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.util.reflect.Classes;

public class SmoothJarHashProvider {
  @Inject
  public SmoothJarHashProvider() {}

  public Hash get() {
    String resourcePath = SmoothJarHashProvider.class
        .getClassLoader()
        .getResource(Classes.binaryPath(SmoothJarHashProvider.class))
        .getPath();
    String smoothJarPath = resourcePath
        .substring(0, resourcePath.lastIndexOf('!'))
        .substring("file:".length());
    return calculateHash(Paths.get(smoothJarPath));
  }

  private static Hash calculateHash(Path smoothJarFile) {
    try {
      return Hash.of(smoothJarFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
