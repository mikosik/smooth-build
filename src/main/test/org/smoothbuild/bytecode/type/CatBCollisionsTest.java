package org.smoothbuild.bytecode.type;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.type.TestingCatsB;

public class CatBCollisionsTest extends TestContext {
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
