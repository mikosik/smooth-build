package org.smoothbuild.virtualmachine.evaluate.step;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;
import org.smoothbuild.virtualmachine.testing.TestingVm;

public class StepHashesCollisionTest extends TestingVm {
  @Test
  void each_task_has_different_hash() throws Exception {
    List<Hash> list = new ArrayList<>();
    Set<Hash> set = new HashSet<>();

    addHash(list, set, new CombineStep(bCombine(), bTrace()));
    BInvoke invoke =
        bInvoke(bIntType(), bMethodTuple(bBlob(1), bString("1")), bBool(true), bTuple());
    BTrace trace = bTrace();
    addHash(list, set, new InvokeStep(invoke, trace));
    addHash(list, set, new OrderStep(bOrder(bIntType()), bTrace()));
    addHash(list, set, new OrderStep(bOrder(bBlobType()), bTrace()));
    addHash(list, set, new PickStep(bPick(), bTrace()));
    addHash(list, set, new SelectStep(bSelect(), bTrace()));
  }

  private void addHash(List<Hash> list, Set<Hash> set, Step step) {
    var hash = StepHashes.stepHash(step);
    if (set.contains(hash)) {
      fail("Hash collision for hash " + hash + " index of previous occurrence "
          + list.indexOf(hash));
    } else {
      list.add(hash);
      set.add(hash);
    }
  }
}
