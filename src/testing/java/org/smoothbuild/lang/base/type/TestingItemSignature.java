package org.smoothbuild.lang.base.type;

import java.util.Optional;

public class TestingItemSignature {
  public static ItemSignature itemSignature(Type type, String name) {
    return new ItemSignature(type, name, Optional.empty());
  }
}
