package org.smoothbuild.lang.define;

import static org.smoothbuild.util.io.Paths.removeExtension;

import java.util.Objects;

import org.smoothbuild.fs.space.FilePath;

public class ModPath {
  private final String path;

  public ModPath(String path) {
    this.path = path;
  }

  public static ModPath of(FilePath filePath) {
    return new ModPath(removeExtension(filePath.toString()));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    return o instanceof ModPath that
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
