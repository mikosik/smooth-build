package org.smoothbuild.testing.common;

import static org.smoothbuild.lang.base.Location.location;
import static org.smoothbuild.lang.base.TestingModuleLocation.moduleLocation;

import org.smoothbuild.lang.base.Location;

public class TestingLocation {
  public static Location loc() {
    return loc(11);
  }

  public static Location loc(int line) {
    return location(moduleLocation(), line);
  }
}
