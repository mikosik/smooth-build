package org.smoothbuild.bytecode.type;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.type.TestingCatsB;

public class CategoryBCollisionsTest extends TestContext {
  @Test
  public void collisions() {
    HashMap<Hash, CategoryB> map = new HashMap<>();
    for (CategoryB type : TestingCatsB.ALL_CATS_TO_TEST) {
      Hash hash = type.hash();
      if (map.containsKey(hash)) {
        fail("Hash " + hash + " is used by two types " + type + " and " + map.get(hash) + ".");
      }
      map.put(hash, type);
    }
  }
}
