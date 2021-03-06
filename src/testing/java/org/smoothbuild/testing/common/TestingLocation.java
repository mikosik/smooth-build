package org.smoothbuild.testing.common;

import static org.smoothbuild.lang.base.Location.location;
import static org.smoothbuild.lang.base.ModulePath.modulePath;
import static org.smoothbuild.lang.base.Space.USER;

import java.nio.file.Path;

import org.smoothbuild.lang.base.Location;

public class TestingLocation {
  public static Location loc() {
    return loc(11);
  }

  public static Location loc(int line) {
    String path = "script.smooth";
    return location(modulePath(USER, Path.of(path), path), line);
  }
}
