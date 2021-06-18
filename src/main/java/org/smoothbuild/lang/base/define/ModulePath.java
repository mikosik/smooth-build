package org.smoothbuild.lang.base.define;

import static org.smoothbuild.util.io.Paths.removeExtension;

import java.util.Objects;

public class ModulePath {
  private final String path;

  public ModulePath(String path) {
    this.path = path;
  }

  public static ModulePath of(FilePath file) {
    return new ModulePath(removeExtension(file.prefixedPath()));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    return o instanceof ModulePath that
        && path.equals(that.path);
  }

  @Override
  public int hashCode() {
    return Objects.hash(path);
  }

  @Override
  public String toString() {
    return path;
  }
}
