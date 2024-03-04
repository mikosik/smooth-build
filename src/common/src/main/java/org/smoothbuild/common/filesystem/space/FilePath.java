package org.smoothbuild.common.filesystem.space;

import org.smoothbuild.common.filesystem.base.Path;

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
}
