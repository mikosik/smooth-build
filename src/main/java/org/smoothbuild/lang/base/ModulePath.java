package org.smoothbuild.lang.base;

import static org.smoothbuild.util.Paths.changeExtension;

import java.nio.file.Path;
import java.util.Objects;

/**
 * This class is immutable.
 */
public class ModulePath {
  private final Space space;
  private final Path fullPath;
  private final String shortPath;

  public ModulePath(Space space, Path fullPath, String shortPath) {
    this.space = space;
    this.fullPath = fullPath;
    this.shortPath = shortPath;
  }

  public Space space() {
    return space;
  }

  public Path fullPath() {
    return fullPath;
  }

  public String shortPath() {
    return shortPath;
  }

  public Path nativeJarPath() {
    return changeExtension(fullPath, "jar");
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof ModulePath) {
      ModulePath that = (ModulePath) object;
      return Objects.equals(this.fullPath, that.fullPath) &&
          Objects.equals(this.shortPath, that.shortPath);
    }
    return false;
  }

  @Override
  public final int hashCode() {
    return Objects.hash(fullPath, shortPath);
  }

  @Override
  public String toString() {
    return shortPath + "(" + fullPath + ")";
  }
}
