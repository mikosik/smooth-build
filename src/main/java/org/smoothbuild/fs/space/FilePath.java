package org.smoothbuild.fs.space;

import static java.util.Arrays.asList;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.fs.base.PathS;

/**
 * This class is immutable.
 */
public record FilePath(Space space, PathS path) {

  public static FilePath filePath(Space space, PathS path) {
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
