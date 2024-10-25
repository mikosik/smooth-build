package org.smoothbuild.virtualmachine.bytecode.kind;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BKind;
import org.smoothbuild.virtualmachine.testing.BytecodeTestContext;
import org.smoothbuild.virtualmachine.testing.TestingBKind;

public class BKindCollisionsTest extends BytecodeTestContext {
  @Test
  void collisions() {
    HashMap<Hash, BKind> map = new HashMap<>();
    for (var kindB : TestingBKind.ALL_KINDS_TO_TEST) {
      var hash = kindB.hash();
      if (map.containsKey(hash)) {
        fail("Hash " + hash + " is used by two kinds " + kindB + " and " + map.get(hash) + ".");
      }
      map.put(hash, kindB);
    }
  }
}
