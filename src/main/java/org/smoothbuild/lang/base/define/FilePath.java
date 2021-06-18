package org.smoothbuild.lang.base.define;

import java.nio.file.Path;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.util.io.Paths;

/**
 * This class is immutable.
 */
public record FilePath(Space space, Path path) {

  public static FilePath filePath(Space space, Path path) {
    return new FilePath(space, path);
  }

  public String prefixedPath() {
    return "{" + space().prefix() + "}/" + path;
  }

  public FilePath withExtension(String extension) {
    return new FilePath(space, Paths.changeExtension(path, extension));
  }

  @Override
  public String toString() {
    return prefixedPath();
  }

  public Hash hash() {
    return Hash.of(Hash.of(path.toString()), Hash.of(space().name()));
  }
}
