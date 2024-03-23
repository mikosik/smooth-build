package org.smoothbuild.virtualmachine.bytecode.type;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.testing.TestingCategoryB;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BCategoryCollisionsTest extends TestingVirtualMachine {
  @Test
  public void collisions() {
    HashMap<Hash, BCategory> map = new HashMap<>();
    for (var categoryB : TestingCategoryB.ALL_CATS_TO_TEST) {
      var hash = categoryB.hash();
      if (map.containsKey(hash)) {
        fail("Hash " + hash + " is used by two types " + categoryB + " and " + map.get(hash) + ".");
      }
      map.put(hash, categoryB);
    }
  }
}
