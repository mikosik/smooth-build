package org.smoothbuild.lang.base.define;

import java.nio.file.Path;

import org.smoothbuild.db.hashed.Hash;

/**
 * This class is immutable.
 */
public record FileLocation(SModule module, Path path) {

  public static FileLocation fileLocation(SModule module, Path path) {
    return new FileLocation(module, path);
  }

  public Space space() {
    return module.space();
  }

  public String prefixedPath() {
    return "{" + space().prefix() + "}/" + path;
  }

  @Override
  public String toString() {
    return prefixedPath();
  }

  public Hash hash() {
    return Hash.of(Hash.of(path.toString()), Hash.of(space().name()));
  }
}
