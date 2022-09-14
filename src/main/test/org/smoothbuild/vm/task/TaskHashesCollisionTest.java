package org.smoothbuild.vm.task;

import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.vm.task.TaskHashes.combineTaskHash;
import static org.smoothbuild.vm.task.TaskHashes.invokeTaskHash;
import static org.smoothbuild.vm.task.TaskHashes.orderTaskHash;
import static org.smoothbuild.vm.task.TaskHashes.selectTaskHash;

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
    addHash(list, set, combineTaskHash(tupleTB()));
    addHash(list, set, combineTaskHash(tupleTB(intTB())));
    addHash(list, set, combineTaskHash(tupleTB(stringTB())));
    addHash(list, set, combineTaskHash(tupleTB(intTB(), stringTB())));
    addHash(list, set, invokeTaskHash(
        methodB(methodTB(intTB()), blobB(1), stringB("1"), boolB(true))));
    addHash(list, set, invokeTaskHash(
        methodB(methodTB(intTB()), blobB(1), stringB("1"), boolB(false))));
    addHash(list, set, invokeTaskHash(
        methodB(methodTB(intTB()), blobB(1), stringB("2"), boolB(true))));
    addHash(list, set, invokeTaskHash(
        methodB(methodTB(intTB()), blobB(2), stringB("1"), boolB(true))));
    addHash(list, set, invokeTaskHash(
        methodB(methodTB(boolTB()), blobB(1), stringB("1"), boolB(true))));
    addHash(list, set, orderTaskHash(arrayTB(intTB())));
    addHash(list, set, orderTaskHash(arrayTB(boolTB())));
    addHash(list, set, selectTaskHash());
  }

  private void addHash(List<Hash> list, Set<Hash> set, Hash hash) {
    if (set.contains(hash)) {
      fail("Hash collision for hash " + hash + " index of previous occurrence "
          + list.indexOf(hash));
    } else {
      list.add(hash);
      set.add(hash);
    }
  }
}
