package org.smoothbuild.lang.base;

import static org.smoothbuild.lang.base.Location.location;
import static org.smoothbuild.lang.base.ModuleLocation.moduleLocation;
import static org.smoothbuild.lang.base.Space.USER;

import java.nio.file.Path;

public class TestingModuleLocation {
  public static Location mLoc(int line) {
    return location(moduleLocation(USER, Path.of("build.smooth")), line);
  }
}
