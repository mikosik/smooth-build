package org.smoothbuild.vm.bytecode.type;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.type.TestingCatsB;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public class CategoryBCollisionsTest extends TestContext {
  @Test
  public void collisions() {
    HashMap<Hash, CategoryB> map = new HashMap<>();
    for (var categoryB : TestingCatsB.ALL_CATS_TO_TEST) {
      var hash = categoryB.hash();
      if (map.containsKey(hash)) {
        fail("Hash " + hash + " is used by two types " + categoryB + " and " + map.get(hash) + ".");
      }
      map.put(hash, categoryB);
    }
  }
}
