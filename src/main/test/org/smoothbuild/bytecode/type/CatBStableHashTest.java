package org.smoothbuild.bytecode.type;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.type.base.CatB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.testing.TestingContext;

public class CatBStableHashTest extends TestingContext {
  @Test
  public void hashes_of_types_are_stable() {
    assertHash(anyTB(), "b35d79d5718f7bba2cda55c29e2408c13ffc8cd5");
    assertHash(blobTB(), "0a2b2a825165ae9742c63b0c6ddafc22f0bd3b1e");
    assertHash(boolTB(), "47f9cc533a5f0c6f650ff0528c0d54d6d2d9d9ab");
    assertHash(funcTB(), "211c92f19379db3f256073c3bd5dc7a2d2d573a8");
    assertHash(intTB(), "8be891c7170d4d1bbca0ffcb3fce71285c55aee4");
    assertHash(methodTB(), "df710c027393cd0d6b465b72b5778b320599c86e");
    assertHash(nothingTB(), "de248ad7b14cbd4e62207297826e21c2aaae36f4");
    assertHash(stringTB(), "5ac99f914f66deae94b7b0d990e821fe2117cf61");
    assertHash(tupleTB(blobTB()), "cd811460be1ebf123cab1361cfca1f49dd5c29c5");
    assertHash(varB("A"), "99f61661c131c5db3723bdaa5ad26229d06dc6ce");

    assertHash(arrayTB(anyTB()), "2dfdcb5ccf6df3057cb84565af5b67f64c685e9c");
    assertHash(arrayTB(blobTB()), "7fbebe7b9e6730b6b49fbd19811677bbd1d8880b");
    assertHash(arrayTB(boolTB()), "b2d929df4b382081405170f09fbe0febb32f547d");
    assertHash(arrayTB(funcTB()), "46d3e489cd21cc8d7011868ddcd167429c96eefb");
    assertHash(arrayTB(methodTB()), "fef60cc8680b040c1c9575f490f049c41c2d3a8b");
    assertHash(arrayTB(intTB()), "ff10a58462549d6f2a55c51be139ae52b62bf801");
    assertHash(arrayTB(nothingTB()), "223052eec1e2f74ed0234fd1ba43f4d49e7e43db");
    assertHash(arrayTB(stringTB()), "dfeac8190688130683d51d719055d46c47cec4d2");
    assertHash(arrayTB(tupleTB(blobTB())), "e843737d83eb150a51a81d9e43a00142982bd959");
    assertHash(arrayTB(varB("A")), "5eae2a05ffe2fee34c07a74779e5eecde3521849");

    assertHash(callCB(intTB()), "f8e8b1d061fcdfb8be52b97cf80e50bc908e59ef");
    assertHash(combineCB(), "7dfcc1569b044dbec3b0e077cff6737ceb0aa99f");
    assertHash(combineCB(intTB()), "3bd468b1587f7bf1921f072e28a7d3b20c8ac70f");
    assertHash(ifCB(), "e89ed0d7c6959af5198d638247cd321ce682fb9a");
    assertHash(invokeCB(), "d8b0ca5e3fc8209c6daff3e081f2ff9d7797395e");
    assertHash(mapCB(), "65476262ff92ca208a8405de21f4e84e7e817d46");
    assertHash(orderCB(intTB()), "04c4786e96bdac890b5089eee99173cd41cecb33");
    assertHash(paramRefCB(intTB()), "fca4f7c553d3189b050352933410a5b8b6ceadb6");
    assertHash(selectCB(intTB()), "bc511a321ba8a722155574c39f0cb5f58ee84a71");
  }

  private static void assertHash(CatB type, String hash) {
    assertThat(type.hash())
        .isEqualTo(Hash.decode(hash));
  }
}
