package org.smoothbuild.common.filesystem.base;

import static org.smoothbuild.common.filesystem.base.FullPath.fullPath;

public record Alias(String name) {
  public static Alias alias(String name) {
    return new Alias(name);
  }

  public FullPath root() {
    return append(Path.root());
  }

  public FullPath append(String path) {
    return append(Path.path(path));
  }

  public FullPath append(Path path) {
    return fullPath(this, path);
  }

  @Override
  public String toString() {
    return "'" + name + "'";
  }
}
