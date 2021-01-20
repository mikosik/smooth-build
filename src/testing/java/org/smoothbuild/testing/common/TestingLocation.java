package org.smoothbuild.testing.common;

import static org.smoothbuild.lang.base.define.Location.location;
import static org.smoothbuild.lang.base.define.TestingModuleLocation.moduleLocation;

import org.smoothbuild.lang.base.define.Location;

public class TestingLocation {
  public static Location loc() {
    return loc(11);
  }

  public static Location loc(int line) {
    return location(moduleLocation(), line);
  }
}
