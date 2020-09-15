package org.smoothbuild.testing.common;

import static org.smoothbuild.lang.base.Location.location;
import static org.smoothbuild.lang.base.ModuleLocation.moduleLocation;
import static org.smoothbuild.lang.base.Space.USER;

import java.nio.file.Path;

import org.smoothbuild.lang.base.Location;

public class TestingLocation {
  public static Location loc() {
    return loc(11);
  }

  public static Location loc(int line) {
    String path = "build.smooth";
    return location(moduleLocation(USER, Path.of(path)), line);
  }
}
