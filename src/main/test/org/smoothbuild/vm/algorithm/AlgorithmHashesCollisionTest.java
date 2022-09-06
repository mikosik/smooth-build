package org.smoothbuild.vm.algorithm;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.testing.TestContext;

public class AlgorithmHashesCollisionTest extends TestContext {

  @Test
  public void each_algorithm_has_different_hash() {
    List<Hash> list = new ArrayList<>();
    Set<Hash> set = new HashSet<>();
    addHash(list, set, AlgorithmHashes.combineAlgorithmHash(tupleTB()));
    addHash(list, set, AlgorithmHashes.combineAlgorithmHash(tupleTB(intTB())));
    addHash(list, set, AlgorithmHashes.combineAlgorithmHash(tupleTB(stringTB())));
    addHash(list, set, AlgorithmHashes.combineAlgorithmHash(tupleTB(intTB(), stringTB())));
    addHash(list, set, AlgorithmHashes.convertAlgorithmHash(intTB()));
    addHash(list, set, AlgorithmHashes.convertAlgorithmHash(stringTB()));
    addHash(list, set, AlgorithmHashes.invokeAlgorithmHash(
        methodB(methodTB(intTB()), blobB(1), stringB("1"), boolB(true))));
    addHash(list, set, AlgorithmHashes.invokeAlgorithmHash(
        methodB(methodTB(intTB()), blobB(1), stringB("1"), boolB(false))));
    addHash(list, set, AlgorithmHashes.invokeAlgorithmHash(
        methodB(methodTB(intTB()), blobB(1), stringB("2"), boolB(true))));
    addHash(list, set, AlgorithmHashes.invokeAlgorithmHash(
        methodB(methodTB(intTB()), blobB(2), stringB("1"), boolB(true))));
    addHash(list, set, AlgorithmHashes.invokeAlgorithmHash(
        methodB(methodTB(boolTB()), blobB(1), stringB("1"), boolB(true))));
    addHash(list, set, AlgorithmHashes.orderAlgorithmHash(arrayTB(intTB())));
    addHash(list, set, AlgorithmHashes.orderAlgorithmHash(arrayTB(boolTB())));
    addHash(list, set, AlgorithmHashes.selectAlgorithmHash());
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
