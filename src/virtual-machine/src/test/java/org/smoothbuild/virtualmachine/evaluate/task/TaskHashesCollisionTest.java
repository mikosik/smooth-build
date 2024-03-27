package org.smoothbuild.virtualmachine.evaluate.task;

import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.virtualmachine.evaluate.task.InvokeTask.newInvokeTask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class TaskHashesCollisionTest extends TestingVirtualMachine {
  @Test
  public void each_task_has_different_hash() throws Exception {
    List<Hash> list = new ArrayList<>();
    Set<Hash> set = new HashSet<>();

    addHash(list, set, new CombineTask(bCombine(), bTrace()));
    addHash(list, set, new ConstTask(bInt(7), bTrace()));
    addHash(list, set, new ConstTask(bInt(9), bTrace()));
    addHash(
        list,
        set,
        newInvokeTask(
            bInvoke(bIntType(), bBlob(1), bString("1"), bBool(true), bTuple()), bTrace()));
    addHash(
        list,
        set,
        newInvokeTask(
            bInvoke(bIntType(), bBlob(1), bString("1"), bBool(true), bTuple(bInt(1))), bTrace()));
    addHash(
        list,
        set,
        newInvokeTask(
            bInvoke(bIntType(), bBlob(1), bString("1"), bBool(false), bTuple()), bTrace()));
    addHash(
        list,
        set,
        newInvokeTask(
            bInvoke(bIntType(), bBlob(1), bString("2"), bBool(true), bTuple()), bTrace()));
    addHash(
        list,
        set,
        newInvokeTask(
            bInvoke(bIntType(), bBlob(2), bString("1"), bBool(true), bTuple()), bTrace()));
    addHash(
        list,
        set,
        newInvokeTask(
            bInvoke(bBoolType(), bBlob(1), bString("1"), bBool(true), bTuple()), bTrace()));
    addHash(list, set, new OrderTask(bOrder(bIntType()), bTrace()));
    addHash(list, set, new OrderTask(bOrder(bBlobType()), bTrace()));
    addHash(list, set, new PickTask(bPick(), bTrace()));
    addHash(list, set, new SelectTask(bSelect(), bTrace()));
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
