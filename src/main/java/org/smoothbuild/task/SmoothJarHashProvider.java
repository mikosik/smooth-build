package org.smoothbuild.task;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.util.reflect.Classes;

import com.google.common.hash.HashCode;

public class SmoothJarHashProvider {
  @Inject
  public SmoothJarHashProvider() {}

  public HashCode get() {
    String resourcePath = SmoothJarHashProvider.class
        .getClassLoader()
        .getResource(Classes.binaryPath(SmoothJarHashProvider.class))
        .getPath();
    String smoothJarPath = resourcePath
        .substring(0, resourcePath.lastIndexOf('!'))
        .substring("file:".length());
    return calculateHash(Paths.get(smoothJarPath));
  }

  private static HashCode calculateHash(Path smoothJarFile) {
    try {
      return Hash.file(smoothJarFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
