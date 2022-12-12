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
    assertHash(closureCB(), "14c06a1154a5de3035f018de523ec56a8e265989");
    assertHash(exprFuncCB(), "f744dc8da04f0c45e090796930bad6b27f69ab69");
    assertHash(funcTB(), "4c8cfe5418defbb9916b07694dc6ff62f7ff350c");
    assertHash(ifFuncCB(), "0619a0edb031c6d43023768c948944922f2fe9a3");
    assertHash(intTB(), "47f9cc533a5f0c6f650ff0528c0d54d6d2d9d9ab");
    assertHash(mapFuncCB(), "c7b955e1f490950d22cf2f3aae1c2ad5dddd9b8a");
    assertHash(nativeFuncCB(), "c4e2d1aa6b39521adb3edd17e94df9d299f5e730");
    assertHash(stringTB(), "7f6f2772815fcab6f5257c71e712f59aafda6757");
    assertHash(tupleTB(blobTB()), "b35e2f19ee80bd23b60555ca35c0b4818bf02381");

    assertHash(arrayTB(blobTB()), "cf55fc8109ca1c153f43d92fc59b5d29f40c836e");
    assertHash(arrayTB(boolTB()), "91412a917a3ee1ede971cc59acd56d864895ff3f");
    assertHash(arrayTB(funcTB()), "b02a3cde8b64bc20c39708aa6d3fc3d3d460de03");
    assertHash(arrayTB(intTB()), "d59245a673844f95bef458e0c0237882f2e7c3ad");
    assertHash(arrayTB(stringTB()), "1ffe3fba8fa6fd7141505ec994a97261cf516369");
    assertHash(arrayTB(tupleTB(blobTB())), "1d73ae3fd6da8970c2059cdf7316d9e1d0cc533d");

    assertHash(callCB(intTB()), "93463aa95e883e4d5f33d295bba45c5e41c01a04");
    assertHash(combineCB(), "eced763485dfe3cf62431dcfaca0345aa9ed436d");
    assertHash(combineCB(intTB()), "ab67fcfb2ff1bcfe601019231f1332aeb11bc3e3");
    assertHash(orderCB(intTB()), "c9918a676b1fee41885c7993a262f9cf5e733345");
    assertHash(pickCB(intTB()), "fa34b9fa7a32b4cfe3ca094f1e00c329d4e75e47");
    assertHash(refCB(intTB()), "abecf47b6bf69f0198fdc49ade92d14379759db7");
    assertHash(selectCB(intTB()), "342238445679fe525c975544fc216f01961650df");
  }

  private static void assertHash(CategoryB type, String hash) {
    assertThat(type.hash())
        .isEqualTo(Hash.decode(hash));
  }
}
