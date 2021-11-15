package org.smoothbuild.db.object.type;

import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.db.object.type.TestingTypesH.ALL_TYPES_TO_TEST;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.testing.TestingContext;

public class TypeHCollisionsTest extends TestingContext {
  @Test
  public void collisions() {
    HashMap<Hash, TypeH> map = new HashMap<>();
    for (TypeH type : ALL_TYPES_TO_TEST) {
      Hash hash = type.hash();
      if (map.containsKey(hash)) {
        fail("Hash " + hash + " is used by two types " + type + " and " + map.get(hash) + ".");
      }
      map.put(hash, type);
    }
  }
}
