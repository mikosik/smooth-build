package org.smoothbuild.exec.algorithm;

import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.combineAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.invokeAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.orderAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.selectAlgorithmHash;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContext;

public class AlgorithmHashesCollisionTest extends TestingContext {

  @Test
  public void each_algorithm_has_different_hash() {
    List<Hash> list = new ArrayList<>();
    Set<Hash> set = new HashSet<>();
    addHash(list, set, combineAlgorithmHash(tupleTH(list())));
    addHash(list, set, combineAlgorithmHash(tupleTH(list(intTH()))));
    addHash(list, set, combineAlgorithmHash(tupleTH(list(stringTH()))));
    addHash(list, set, combineAlgorithmHash(tupleTH(list(intTH(), stringTH()))));
    addHash(list, set, invokeAlgorithmHash(
        methodH(methodTH(intTH(), list()), blobH(1), stringH("1"), boolH(true))));
    addHash(list, set, invokeAlgorithmHash(
        methodH(methodTH(intTH(), list()), blobH(1), stringH("1"), boolH(false))));
    addHash(list, set, invokeAlgorithmHash(
        methodH(methodTH(intTH(), list()), blobH(1), stringH("2"), boolH(true))));
    addHash(list, set, invokeAlgorithmHash(
        methodH(methodTH(intTH(), list()), blobH(2), stringH("1"), boolH(true))));
    addHash(list, set, invokeAlgorithmHash(
        methodH(methodTH(boolTH(), list()), blobH(1), stringH("1"), boolH(true))));
    addHash(list, set, orderAlgorithmHash());
    addHash(list, set, selectAlgorithmHash());
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
