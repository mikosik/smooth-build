package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.type.Types.string;

import java.util.Optional;

public class TestingItem {
  public static Item item(String name) {
    return new Item(string(), name, Optional.empty());
  }
}
