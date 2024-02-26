package org.smoothbuild.common.filesystem.space;

import org.smoothbuild.common.filesystem.base.PathS;

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
}
