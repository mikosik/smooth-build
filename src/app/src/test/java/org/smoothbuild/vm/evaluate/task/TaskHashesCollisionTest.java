package org.smoothbuild.vm.evaluate.task;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public class TaskHashesCollisionTest extends TestContext {
  @Test
  public void each_task_has_different_hash() {
    List<Hash> list = new ArrayList<>();
    Set<Hash> set = new HashSet<>();

    addHash(list, set, new CombineTask(combineB(), traceB()));
    addHash(list, set, new ConstTask(intB(7), traceB()));
    addHash(list, set, new ConstTask(intB(9), traceB()));
    addHash(
        list,
        set,
        new InvokeTask(
            callB(), nativeFuncB(funcTB(intTB()), blobB(1), stringB("1"), boolB(true)), traceB()));
    addHash(
        list,
        set,
        new InvokeTask(
            callB(), nativeFuncB(funcTB(intTB()), blobB(1), stringB("1"), boolB(false)), traceB()));
    addHash(
        list,
        set,
        new InvokeTask(
            callB(), nativeFuncB(funcTB(intTB()), blobB(1), stringB("2"), boolB(true)), traceB()));
    addHash(
        list,
        set,
        new InvokeTask(
            callB(), nativeFuncB(funcTB(intTB()), blobB(2), stringB("1"), boolB(true)), traceB()));
    addHash(
        list,
        set,
        new InvokeTask(
            callB(), nativeFuncB(funcTB(boolTB()), blobB(1), stringB("1"), boolB(true)), traceB()));
    addHash(list, set, new OrderTask(orderB(intTB()), traceB()));
    addHash(list, set, new OrderTask(orderB(blobTB()), traceB()));
    addHash(list, set, new PickTask(pickB(), traceB()));
    addHash(list, set, new SelectTask(selectB(), traceB()));
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
