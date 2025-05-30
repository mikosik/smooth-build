package org.smoothbuild.virtualmachine.evaluate.step;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class StepHashesCollisionTest extends VmTestContext {
  @Test
  void each_task_has_different_hash() throws Exception {
    List<Hash> list = new ArrayList<>();
    Set<Hash> set = new HashSet<>();

    addHash(list, set, new ChooseStep(bChoose(), trace()));
    addHash(list, set, new CombineStep(bCombine(), trace()));
    var invoke = bInvoke(bIntType(), bMethodTuple(bBlob(1), bString("1")), bBool(true), bTuple());
    addHash(list, set, new InvokeStep(invoke, trace()));
    addHash(list, set, new OrderStep(bOrder(bIntType()), trace()));
    addHash(list, set, new OrderStep(bOrder(bBlobType()), trace()));
    addHash(list, set, new PickStep(bPick(), trace()));
    addHash(list, set, new SelectStep(bSelect(), trace()));
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
