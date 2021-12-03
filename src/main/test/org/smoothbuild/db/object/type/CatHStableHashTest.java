package org.smoothbuild.db.object.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.CatH;
import org.smoothbuild.testing.TestingContext;

public class CatHStableHashTest extends TestingContext {
  @Test
  public void hashes_of_types_are_stable() {
    assertHash(anyTH(), "b35d79d5718f7bba2cda55c29e2408c13ffc8cd5");
    assertHash(blobTH(), "0a2b2a825165ae9742c63b0c6ddafc22f0bd3b1e");
    assertHash(boolTH(), "47f9cc533a5f0c6f650ff0528c0d54d6d2d9d9ab");
    assertHash(abstFuncTH(), "72f4e3d7262b25cf65222b035b02e520c32f7a30");
    assertHash(ifFuncTH(), "68d16d970d8838ccdb4a0afd0c098f9797bb54fb");
    assertHash(intTH(), "8be891c7170d4d1bbca0ffcb3fce71285c55aee4");
    assertHash(mapFuncTH(), "32ffed4cade9499f1500c03b059e6d8a8acac6f5");
    assertHash(natFuncTH(), "2e252d8c6c89bfe0a596798718275974452761a6");
    assertHash(nothingTH(), "de248ad7b14cbd4e62207297826e21c2aaae36f4");
    assertHash(stringTH(), "5ac99f914f66deae94b7b0d990e821fe2117cf61");
    assertHash(tupleTH(list(blobTH())), "cd811460be1ebf123cab1361cfca1f49dd5c29c5");
    assertHash(varTH("A"), "99f61661c131c5db3723bdaa5ad26229d06dc6ce");

    assertHash(arrayTH(anyTH()), "2dfdcb5ccf6df3057cb84565af5b67f64c685e9c");
    assertHash(arrayTH(blobTH()), "7fbebe7b9e6730b6b49fbd19811677bbd1d8880b");
    assertHash(arrayTH(boolTH()), "b2d929df4b382081405170f09fbe0febb32f547d");
    assertHash(arrayTH(abstFuncTH()), "35f599db587ed8b7b52bb0ce3a07a2d4d9c1c223");
    assertHash(arrayTH(intTH()), "ff10a58462549d6f2a55c51be139ae52b62bf801");
    assertHash(arrayTH(nothingTH()), "223052eec1e2f74ed0234fd1ba43f4d49e7e43db");
    assertHash(arrayTH(stringTH()), "dfeac8190688130683d51d719055d46c47cec4d2");
    assertHash(arrayTH(tupleTH(list(blobTH()))), "e843737d83eb150a51a81d9e43a00142982bd959");
    assertHash(arrayTH(varTH("A")), "5eae2a05ffe2fee34c07a74779e5eecde3521849");

    assertHash(orderCH(intTH()), "04c4786e96bdac890b5089eee99173cd41cecb33");
    assertHash(callCH(intTH()), "f8e8b1d061fcdfb8be52b97cf80e50bc908e59ef");
    assertHash(paramRefCH(intTH()), "fca4f7c553d3189b050352933410a5b8b6ceadb6");
    assertHash(selectCH(intTH()), "bc511a321ba8a722155574c39f0cb5f58ee84a71");
  }

  private static void assertHash(CatH type, String hash) {
    assertThat(type.hash())
        .isEqualTo(Hash.decode(hash));
  }
}
