package org.smoothbuild.virtualmachine.bytecode.type;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.testing.TestingBKind;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BKindCollisionsTest extends TestingVirtualMachine {
  @Test
  public void collisions() {
    HashMap<Hash, BKind> map = new HashMap<>();
    for (var kindB : TestingBKind.ALL_CATS_TO_TEST) {
      var hash = kindB.hash();
      if (map.containsKey(hash)) {
        fail("Hash " + hash + " is used by two types " + kindB + " and " + map.get(hash) + ".");
      }
      map.put(hash, kindB);
    }
  }
}
