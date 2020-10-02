package org.smoothbuild.lang.base.type;

import static org.smoothbuild.testing.common.TestingLocation.loc;

import java.util.Optional;

public class TestingItemSignature {
  public static ItemSignature item(Type type, String name) {
    return new ItemSignature(type, name, Optional.of(type), loc(7));
  }
}
