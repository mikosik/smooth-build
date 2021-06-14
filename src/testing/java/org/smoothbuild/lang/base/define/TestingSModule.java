package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.Space.USER;
import static org.smoothbuild.util.Lists.list;

import java.nio.file.Path;

public class TestingSModule {
  public static SModule module(String fileName) {
    return new SModule(USER, Path.of(fileName), list());
  }
}
