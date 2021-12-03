package org.smoothbuild.exec.algorithm;

import static okio.ByteString.encodeUtf8;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.combineAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.constAlgorithmHash;
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
    addHash(list, set, constAlgorithmHash(blobH(encodeUtf8("abc"))));
    addHash(list, set, constAlgorithmHash(blobH(encodeUtf8("def"))));
    addHash(list, set, constAlgorithmHash(boolH(false)));
    addHash(list, set, constAlgorithmHash(boolH(true)));
    addHash(list, set, constAlgorithmHash(defFuncH(intH())));
    addHash(list, set, constAlgorithmHash(natFuncH(blobH(), stringH("binaryName"))));
    addHash(list, set, constAlgorithmHash(mapFuncH()));
    addHash(list, set, constAlgorithmHash(ifFuncH()));
    addHash(list, set, constAlgorithmHash(intH(0)));
    addHash(list, set, constAlgorithmHash(intH(1)));
    addHash(list, set, constAlgorithmHash(stringH("abc")));
    addHash(list, set, constAlgorithmHash(stringH("def")));
    addHash(list, set, constAlgorithmHash(tupleH(list(intH(0)))));
    addHash(list, set, constAlgorithmHash(tupleH(list(stringH("abc")))));
    addHash(list, set, constAlgorithmHash(arrayH(blobTH())));
    addHash(list, set, constAlgorithmHash(arrayH(blobH(encodeUtf8("abc")))));
    addHash(list, set, constAlgorithmHash(arrayH(blobH(encodeUtf8("def")))));
    addHash(list, set, constAlgorithmHash(arrayH(boolTH())));
    addHash(list, set, constAlgorithmHash(arrayH(boolH(false))));
    addHash(list, set, constAlgorithmHash(arrayH(boolH(true))));
    addHash(list, set, constAlgorithmHash(arrayH(defFuncH(intH()))));
    addHash(list, set, constAlgorithmHash(arrayH(natFuncH(blobH(), stringH("binaryName")))));
    addHash(list, set, constAlgorithmHash(arrayH(mapFuncH())));
    addHash(list, set, constAlgorithmHash(arrayH(ifFuncH())));
    addHash(list, set, constAlgorithmHash(arrayH(intTH())));
    addHash(list, set, constAlgorithmHash(arrayH(intH(0))));
    addHash(list, set, constAlgorithmHash(arrayH(stringTH())));
    addHash(list, set, constAlgorithmHash(arrayH(stringH("abc"))));
    addHash(list, set, constAlgorithmHash(arrayH(tupleH(list(intH(0))))));
    addHash(list, set, constAlgorithmHash(arrayH(tupleH(list(stringH("abc"))))));
    addHash(list, set, combineAlgorithmHash(tupleTH(list())));
    addHash(list, set, combineAlgorithmHash(tupleTH(list(intTH()))));
    addHash(list, set, combineAlgorithmHash(tupleTH(list(stringTH()))));
    addHash(list, set, combineAlgorithmHash(tupleTH(list(intTH(), stringTH()))));
    addHash(list, set, invokeAlgorithmHash(natFuncH(blobH(encodeUtf8("blob 1")), stringH("class 1"))));
    addHash(list, set, invokeAlgorithmHash(natFuncH(blobH(encodeUtf8("blob 1")), stringH("class 2"))));
    addHash(list, set, invokeAlgorithmHash(natFuncH(blobH(encodeUtf8("blob 2")), stringH("class 1"))));
    addHash(list, set, invokeAlgorithmHash(natFuncH(blobH(encodeUtf8("blob 2")), stringH("class 2"))));
    addHash(list, set, orderAlgorithmHash());
    addHash(list, set, selectAlgorithmHash(intH(0)));
    addHash(list, set, selectAlgorithmHash(intH(1)));
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
