package org.smoothbuild.testing.common;

import static org.smoothbuild.lang.base.Location.location;
import static org.smoothbuild.lang.base.Space.USER;

import java.nio.file.Path;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.ModulePath;

public class TestingLocation {
  public static Location loc() {
    return loc(11);
  }

  public static Location loc(int line) {
    String path = "script.smooth";
    return location(new ModulePath(USER, Path.of(path), path), line);
  }
}
