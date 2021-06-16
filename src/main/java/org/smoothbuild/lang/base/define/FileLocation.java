package org.smoothbuild.lang.base.define;

import java.nio.file.Path;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.util.io.Paths;

/**
 * This class is immutable.
 */
public record FileLocation(Space space, Path path) {

  public static FileLocation fileLocation(Space space, Path path) {
    return new FileLocation(space, path);
  }

  public String prefixedPath() {
    return "{" + space().prefix() + "}/" + path;
  }

  public FileLocation withExtension(String extension) {
    return new FileLocation(space, Paths.changeExtension(path, extension));
  }

  @Override
  public String toString() {
    return prefixedPath();
  }

  public Hash hash() {
    return Hash.of(Hash.of(path.toString()), Hash.of(space().name()));
  }
}
