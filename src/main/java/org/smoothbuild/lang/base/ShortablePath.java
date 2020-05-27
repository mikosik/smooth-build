package org.smoothbuild.lang.base;

import java.nio.file.Path;
import java.util.Objects;

/**
 * This class is immutable.
 */
public class ShortablePath {
  private final Path path;
  private final String shorted;

  public ShortablePath(Path path, String shorted) {
    this.path = path;
    this.shorted = shorted;
  }

  public Path path() {
    return path;
  }

  public String shorted() {
    return shorted;
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof ShortablePath) {
      ShortablePath that = (ShortablePath) object;
      return Objects.equals(this.path, that.path) &&
          Objects.equals(this.shorted, that.shorted);
    }
    return false;
  }

  @Override
  public final int hashCode() {
    return Objects.hash(path, shorted);
  }

  @Override
  public String toString() {
    return shorted + "(" + path + ")";
  }
}
