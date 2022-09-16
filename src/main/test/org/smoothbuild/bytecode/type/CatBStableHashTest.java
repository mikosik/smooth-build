package org.smoothbuild.bytecode.type;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.testing.TestContext;

public class CatBStableHashTest extends TestContext {
  @Test
  public void hashes_of_types_are_stable() {
    assertHash(blobTB(), "7ab8dc8456c25f132551f157c77a1888ef918fac");
    assertHash(boolTB(), "0a2b2a825165ae9742c63b0c6ddafc22f0bd3b1e");
    assertHash(funcTB(), "a17cb63e74d0f86a0e179a62c50a7cee16e99a00");
    assertHash(intTB(), "47f9cc533a5f0c6f650ff0528c0d54d6d2d9d9ab");
    assertHash(methodTB(), "3ad46d5fe7c2aae1930850ad5d0d590ab8a7c74c");
    assertHash(stringTB(), "7f6f2772815fcab6f5257c71e712f59aafda6757");
    assertHash(tupleTB(blobTB()), "b35e2f19ee80bd23b60555ca35c0b4818bf02381");

    assertHash(arrayTB(blobTB()), "cf55fc8109ca1c153f43d92fc59b5d29f40c836e");
    assertHash(arrayTB(boolTB()), "91412a917a3ee1ede971cc59acd56d864895ff3f");
    assertHash(arrayTB(funcTB()), "b3dabb2bf12708c074197ae202575d16e3c96249");
    assertHash(arrayTB(methodTB()), "25dc2e96c5cbb912ef2db2067f45b818ed03b4d1");
    assertHash(arrayTB(intTB()), "d59245a673844f95bef458e0c0237882f2e7c3ad");
    assertHash(arrayTB(stringTB()), "1ffe3fba8fa6fd7141505ec994a97261cf516369");
    assertHash(arrayTB(tupleTB(blobTB())), "1d73ae3fd6da8970c2059cdf7316d9e1d0cc533d");

    assertHash(callCB(intTB()), "93463aa95e883e4d5f33d295bba45c5e41c01a04");
    assertHash(combineCB(), "eced763485dfe3cf62431dcfaca0345aa9ed436d");
    assertHash(combineCB(intTB()), "ab67fcfb2ff1bcfe601019231f1332aeb11bc3e3");
    assertHash(ifCB(), "314fcc191f67cbe80dc1a592a90aec21a23ca303");
    assertHash(invokeCB(), "c97a0b42f167fb5944497999770dcc10649411cf");
    assertHash(mapCB(), "a5f1e2d0da98524b5400c562e0cd883658bb5f35");
    assertHash(orderCB(intTB()), "c9918a676b1fee41885c7993a262f9cf5e733345");
    assertHash(paramRefCB(intTB()), "abecf47b6bf69f0198fdc49ade92d14379759db7");
    assertHash(selectCB(intTB()), "342238445679fe525c975544fc216f01961650df");
  }

  private static void assertHash(CatB type, String hash) {
    assertThat(type.hash())
        .isEqualTo(Hash.decode(hash));
  }
}
