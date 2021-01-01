package org.smoothbuild.lang.base;

import static org.smoothbuild.lang.base.type.Types.string;

import java.util.Optional;

public class TestingItem {
  public static Item field(String name) {
    return new Item(string(), name, Optional.empty());
  }
}
