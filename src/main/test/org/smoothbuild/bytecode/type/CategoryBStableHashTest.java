package org.smoothbuild.bytecode.type;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.testing.TestContext;

public class CategoryBStableHashTest extends TestContext {
  @Test
  public void hashes_of_types_are_stable() {
    assertHash(blobTB(), "7ab8dc8456c25f132551f157c77a1888ef918fac");
    assertHash(boolTB(), "0a2b2a825165ae9742c63b0c6ddafc22f0bd3b1e");
    assertHash(defFuncCB(), "98974a12d884e4d69ea57ea27ac3e7cba43ccce5");
    assertHash(ifFuncCB(), "7665ae39e1bc549c1a67f8912dc3afc4ae6800fa");
    assertHash(intTB(), "47f9cc533a5f0c6f650ff0528c0d54d6d2d9d9ab");
    assertHash(mapFuncCB(), "518c920d53245d17d91740bf754a3795a01d73b7");
    assertHash(natFuncCB(), "b59ff771db774d44190856a15efaa2fde25138e8");
    assertHash(stringTB(), "7f6f2772815fcab6f5257c71e712f59aafda6757");
    assertHash(tupleTB(blobTB()), "b35e2f19ee80bd23b60555ca35c0b4818bf02381");

    assertHash(arrayTB(blobTB()), "cf55fc8109ca1c153f43d92fc59b5d29f40c836e");
    assertHash(arrayTB(boolTB()), "91412a917a3ee1ede971cc59acd56d864895ff3f");
    assertHash(arrayTB(funcTB()), "7f7d9f08b5ec8200a98f7a360e33d4707a0d791d");
    assertHash(arrayTB(intTB()), "d59245a673844f95bef458e0c0237882f2e7c3ad");
    assertHash(arrayTB(stringTB()), "1ffe3fba8fa6fd7141505ec994a97261cf516369");
    assertHash(arrayTB(tupleTB(blobTB())), "1d73ae3fd6da8970c2059cdf7316d9e1d0cc533d");

    assertHash(callCB(intTB()), "93463aa95e883e4d5f33d295bba45c5e41c01a04");
    assertHash(combineCB(), "eced763485dfe3cf62431dcfaca0345aa9ed436d");
    assertHash(combineCB(intTB()), "ab67fcfb2ff1bcfe601019231f1332aeb11bc3e3");
    assertHash(orderCB(intTB()), "c9918a676b1fee41885c7993a262f9cf5e733345");
    assertHash(paramRefCB(intTB()), "abecf47b6bf69f0198fdc49ade92d14379759db7");
    assertHash(selectCB(intTB()), "342238445679fe525c975544fc216f01961650df");
  }

  private static void assertHash(CategoryB type, String hash) {
    assertThat(type.hash())
        .isEqualTo(Hash.decode(hash));
  }
}
