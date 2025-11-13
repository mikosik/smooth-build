package org.smoothbuild.virtualmachine.evaluate.evaluator;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOperation;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class OperationHashesCollisionTest extends VmTestContext {
  @Test
  void each_task_has_different_hash() throws Exception {
    List<Hash> list = new ArrayList<>();
    Set<Hash> set = new HashSet<>();

    addHash(list, set, bChoose());
    addHash(list, set, bCombine());
    addHash(
        list,
        set,
        bInvoke(bIntType(), bMethodTuple(bBlob(1), bString("1")), bBool(true), bTuple()));
    addHash(list, set, bOrder(bIntType()));
    addHash(list, set, bOrder(bBlobType()));
    addHash(list, set, bPick());
    addHash(list, set, bSelect());
  }

  private void addHash(List<Hash> list, Set<Hash> set, BOperation operation) {
    var hash = OperationHashes.operationHash(operation);
    if (set.contains(hash)) {
      fail("Hash collision for hash " + hash + " index of previous occurrence "
          + list.indexOf(hash));
    } else {
      list.add(hash);
      set.add(hash);
    }
  }
}
