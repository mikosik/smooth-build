package org.smoothbuild.lang.base.define;

import static org.smoothbuild.util.io.Paths.removeExtension;

import java.util.Objects;

import org.smoothbuild.io.fs.base.FilePath;

public class ModulePath {
  private final String path;

  public ModulePath(String path) {
    this.path = path;
  }

  public static ModulePath of(FilePath filePath) {
    return new ModulePath(removeExtension(filePath.toString()));
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
