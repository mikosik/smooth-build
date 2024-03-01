package org.smoothbuild.virtualmachine.bytecode.type;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.Hash;
import org.smoothbuild.virtualmachine.testing.TestCategoryB;
import org.smoothbuild.virtualmachine.testing.TestVirtualMachine;

public class CategoryBCollisionsTest extends TestVirtualMachine {
  @Test
  public void collisions() {
    HashMap<Hash, CategoryB> map = new HashMap<>();
    for (var categoryB : TestCategoryB.ALL_CATS_TO_TEST) {
      var hash = categoryB.hash();
      if (map.containsKey(hash)) {
        fail("Hash " + hash + " is used by two types " + categoryB + " and " + map.get(hash) + ".");
      }
      map.put(hash, categoryB);
    }
  }
}