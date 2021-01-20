package org.smoothbuild.lang.base.define;

import static org.smoothbuild.util.io.Paths.changeExtension;

import java.nio.file.Path;

import org.smoothbuild.util.io.Paths;

/**
 * This class is immutable.
 */
public record ModuleLocation(Space space, Path path) {

  public static ModuleLocation moduleLocation(Space space, Path path) {
    return new ModuleLocation(space, path);
  }

  public String name() {
    return Paths.removeExtension(path.getFileName().toString());
  }

  public String prefixedPath() {
    return "{" + space.prefix() + "}/" + path;
  }

  public ModuleLocation toNative() {
    return new ModuleLocation(space, changeExtension(path, "jar"));
  }

  @Override
  public String toString() {
    return prefixedPath();
  }
}
