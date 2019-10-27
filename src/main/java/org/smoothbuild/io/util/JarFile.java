package org.smoothbuild.io.util;

import java.io.IOException;
import java.nio.file.Path;

import org.smoothbuild.db.hashed.Hash;

public class JarFile {
  private final Path path;
  private final Hash hash;

  public static JarFile jarFile(Path path) throws IOException {
    return new JarFile(path, Hash.of(path));
  }

  public JarFile(Path path, Hash hash) {
    this.path = path;
    this.hash = hash;
  }

  public Hash hash() {
    return hash;
  }

  public Path path() {
    return path;
  }
}
