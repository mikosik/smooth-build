package org.smoothbuild.io.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.smoothbuild.db.hashed.Hash;

import com.google.common.hash.HashCode;

public class JarFile {
  private final Path path;
  private final HashCode hash;

  public static JarFile jarFile(Path path) throws IOException {
    return new JarFile(path, Hash.file(path));
  }

  public JarFile(Path path, HashCode hash) {
    this.path = path;
    this.hash = hash;
  }

  public HashCode hash() {
    return hash;
  }

  public Path path() {
    return path;
  }

  public InputStream openInputStream() throws FileNotFoundException {
    return new FileInputStream(path.toFile());
  }
}
