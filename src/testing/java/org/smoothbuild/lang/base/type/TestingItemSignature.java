package org.smoothbuild.lang.base.type;

import java.util.Optional;

import org.smoothbuild.lang.base.define.ItemSignature;
import org.smoothbuild.lang.base.type.api.Type;

public class TestingItemSignature {
  public static ItemSignature itemSignature(Type type, String name) {
    return new ItemSignature(type, name, Optional.empty());
  }
}
