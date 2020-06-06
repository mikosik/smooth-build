package org.smoothbuild.lang.base;

import static org.smoothbuild.util.Paths.changeExtension;

import java.nio.file.Path;

import org.smoothbuild.util.Paths;

public record ModulePath(Space space, ShortablePath smooth, ShortablePath nativ) {

  public static ModulePath modulePath(Space space, Path fullPath, String shortPath) {
    return new ModulePath(
        space,
        new ShortablePath(fullPath, shortPath),
        new ShortablePath(
            fullPath == null ? null : changeExtension(fullPath, "jar"),
            shortPath == null ? null : changeExtension(shortPath, "jar")));
  }

  public String name() {
    return Paths.removeExtension(smooth.path().getFileName().toString());
  }

  @Override
  public String toString() {
    return smooth.shorted() + "(" + smooth.path() + ")";
  }
}
