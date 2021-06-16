package org.smoothbuild.lang.base.define;

import java.util.Objects;

public class ModulePath {
  private final String path;

  public ModulePath(String path) {
    this.path = path;
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
