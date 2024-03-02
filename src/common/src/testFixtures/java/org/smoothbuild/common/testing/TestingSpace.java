package org.smoothbuild.common.testing;

import org.smoothbuild.common.filesystem.space.Space;

public class TestingSpace {
  public static Space space(String name) {
    return new MySpace(name);
  }
}

record MySpace(String name) implements Space {
  @Override
  public String prefix() {
    return name;
  }
}
