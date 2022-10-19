package org.smoothbuild.vm.task;

import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.vm.execute.TaskKind.ORDER;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.testing.TestContext;

public class TaskHashesCollisionTest extends TestContext {
  @Test
  public void each_task_has_different_hash() {
    List<Hash> list = new ArrayList<>();
    Set<Hash> set = new HashSet<>();

    addHash(list, set, new CombineTask(tupleTB(), tagLoc(), traceS()));
    addHash(list, set, new ConstTask(intB(7), tagLoc(), traceS()));
    addHash(list, set, new ConstTask(intB(9), tagLoc(), traceS()));
    addHash(list, set, new IdentityTask(intTB(), ORDER, tagLoc(), traceS()));
    addHash(list, set, new NativeCallTask(intTB(), "name",
        natFuncB(funcTB(intTB()), blobB(1), stringB("1"), boolB(true)),
        null, tagLoc(), traceS()));
    addHash(list, set, new NativeCallTask(intTB(), "name",
        natFuncB(funcTB(intTB()), blobB(1), stringB("1"), boolB(false)),
        null, tagLoc(), traceS()));
    addHash(list, set, new NativeCallTask(intTB(), "name",
        natFuncB(funcTB(intTB()), blobB(1), stringB("2"), boolB(true)),
        null, tagLoc(), traceS()));
    addHash(list, set, new NativeCallTask(intTB(), "name",
        natFuncB(funcTB(intTB()), blobB(2), stringB("1"), boolB(true)),
        null, tagLoc(), traceS()));
    addHash(list, set, new NativeCallTask(intTB(), "name",
        natFuncB(funcTB(boolTB()), blobB(1), stringB("1"), boolB(true)),
        null, tagLoc(), traceS()));
    addHash(list, set, new OrderTask(arrayTB(intTB()), tagLoc(), traceS()));
    addHash(list, set, new OrderTask(arrayTB(blobTB()), tagLoc(), traceS()));
    addHash(list, set, new PickTask(intTB(), tagLoc(), traceS()));
    addHash(list, set, new SelectTask(intTB(), tagLoc(), traceS()));
  }

  private void addHash(List<Hash> list, Set<Hash> set, Task task) {
    var hash = TaskHashes.taskHash(task);
    if (set.contains(hash)) {
      fail("Hash collision for hash " + hash + " index of previous occurrence "
          + list.indexOf(hash));
    } else {
      list.add(hash);
      set.add(hash);
    }
  }
}
