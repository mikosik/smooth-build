package org.smoothbuild.io.fs.space;

import static java.util.Arrays.asList;

import org.smoothbuild.db.Hash;
import org.smoothbuild.io.fs.base.Path;

/**
 * This class is immutable.
 */
public record FilePath(Space space, Path path) {

  public static FilePath filePath(Space space, Path path) {
    return new FilePath(space, path);
  }

  public FilePath withExtension(String extension) {
    return filePath(space, path.changeExtension(extension));
  }

  public String q() {
    return "'" + this + "'";
  }

  @Override
  public String toString() {
    return "{" + space().prefix() + "}/" + path;
  }

  public Hash hash() {
    return Hash.of(asList(
        Hash.of(path.toString()),
        Hash.of(space().name())
    ));
  }
}
