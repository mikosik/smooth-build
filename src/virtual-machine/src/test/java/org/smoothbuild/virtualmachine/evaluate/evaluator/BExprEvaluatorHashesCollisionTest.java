package org.smoothbuild.virtualmachine.evaluate.evaluator;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BExprEvaluatorHashesCollisionTest extends VmTestContext {
  @Test
  void each_task_has_different_hash() throws Exception {
    List<Hash> list = new ArrayList<>();
    Set<Hash> set = new HashSet<>();

    addHash(list, set, new BChooseEvaluator(bChoose(), trace()));
    addHash(list, set, new BCombineEvaluator(bCombine(), trace()));
    var invoke = bInvoke(bIntType(), bMethodTuple(bBlob(1), bString("1")), bBool(true), bTuple());
    addHash(list, set, new BInvokeEvaluator(invoke, trace()));
    addHash(list, set, new BOrderEvaluator(bOrder(bIntType()), trace()));
    addHash(list, set, new BOrderEvaluator(bOrder(bBlobType()), trace()));
    addHash(list, set, new BPickEvaluator(bPick(), trace()));
    addHash(list, set, new BSelectEvaluator(bSelect(), trace()));
  }

  private void addHash(List<Hash> list, Set<Hash> set, BExprEvaluator bExprEvaluator) {
    var hash = BExprEvaluatorHashes.evaluatorHash(bExprEvaluator);
    if (set.contains(hash)) {
      fail("Hash collision for hash " + hash + " index of previous occurrence "
          + list.indexOf(hash));
    } else {
      list.add(hash);
      set.add(hash);
    }
  }
}
