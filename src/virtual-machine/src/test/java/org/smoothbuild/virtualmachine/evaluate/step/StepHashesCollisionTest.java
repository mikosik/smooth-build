package org.smoothbuild.virtualmachine.evaluate.step;

import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.virtualmachine.evaluate.step.InvokeStep.newInvokeStep;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class StepHashesCollisionTest extends TestingVirtualMachine {
  @Test
  void each_task_has_different_hash() throws Exception {
    List<Hash> list = new ArrayList<>();
    Set<Hash> set = new HashSet<>();

    addHash(list, set, new CombineStep(bCombine(), bTrace()));
    addHash(list, set, new ConstStep(bInt(7), bTrace()));
    addHash(list, set, new ConstStep(bInt(9), bTrace()));
    addHash(
        list,
        set,
        newInvokeStep(
            bInvoke(bIntType(), bMethodTuple(bBlob(1), bString("1")), bBool(true), bTuple()),
            bTrace()));
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
