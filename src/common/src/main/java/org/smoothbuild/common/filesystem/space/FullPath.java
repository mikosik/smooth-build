package org.smoothbuild.common.filesystem.space;

import org.smoothbuild.common.filesystem.base.Path;

/**
 * This class is immutable.
 */
public record FullPath(Space space, Path path) {

  public static FullPath fullPath(Space space, Path path) {
    return new FullPath(space, path);
  }

  public FullPath withExtension(String extension) {
    return fullPath(space, path.changeExtension(extension));
  }

  public String q() {
    return "'" + this + "'";
  }

  @Override
  public String toString() {
    return "{" + space().prefix() + "}/" + path;
  }
}
