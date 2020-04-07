package org.smoothbuild.testing.common;

import static org.smoothbuild.lang.base.Location.location;

import java.nio.file.Paths;

import org.smoothbuild.ModulePath;
import org.smoothbuild.lang.base.Location;

public class TestingLocation {
  public static Location loc() {
    return loc(11);
  }

  public static Location loc(int line) {
    String path = "script.smooth";
    return location(new ModulePath(Paths.get(path), path), line);
  }
}
