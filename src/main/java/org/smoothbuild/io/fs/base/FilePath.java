package org.smoothbuild.io.fs.base;

import org.smoothbuild.db.hashed.Hash;

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
    return filePath(space, path.changeExtension(extension));
  }

  @Override
  public String toString() {
    return prefixedPath();
  }

  public Hash hash() {
    return Hash.of(Hash.of(path.toString()), Hash.of(space().name()));
  }
}
