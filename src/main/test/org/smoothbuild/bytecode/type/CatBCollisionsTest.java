package org.smoothbuild.bytecode.type;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.type.base.CatB;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContext;

public class CatBCollisionsTest extends TestingContext {
  @Test
  public void collisions() {
    HashMap<Hash, CatB> map = new HashMap<>();
    for (CatB type : TestingCatsB.ALL_CATS_TO_TEST) {
      Hash hash = type.hash();
      if (map.containsKey(hash)) {
        fail("Hash " + hash + " is used by two types " + type + " and " + map.get(hash) + ".");
      }
      map.put(hash, type);
    }
  }
}
