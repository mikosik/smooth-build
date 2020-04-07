package org.smoothbuild;

import java.nio.file.Path;
import java.util.Objects;

public class ModulePath {
  private final Path fullPath;
  private final String shortPath;

  public ModulePath(Path fullPath, String shortPath) {
    this.fullPath = fullPath;
    this.shortPath = shortPath;
  }

  public Path fullPath() {
    return fullPath;
  }

  public String shortPath() {
    return shortPath;
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
