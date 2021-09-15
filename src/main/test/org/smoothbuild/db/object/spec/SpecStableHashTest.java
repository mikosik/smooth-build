package org.smoothbuild.db.object.spec;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.testing.TestingContext;

public class SpecStableHashTest extends TestingContext {
  @Test
  public void hashes_of_specs_are_stable() {
    assertHash(blobSpec(), "0a2b2a825165ae9742c63b0c6ddafc22f0bd3b1e");
    assertHash(boolSpec(), "47f9cc533a5f0c6f650ff0528c0d54d6d2d9d9ab");
    assertHash(definedLambdaSpec(), "72f4e3d7262b25cf65222b035b02e520c32f7a30");
    assertHash(intSpec(), "8be891c7170d4d1bbca0ffcb3fce71285c55aee4");
    assertHash(nativeLambdaSpec(), "3ca49278a2be1cec183e86717a8f0d331af510ea");
    assertHash(nothingSpec(), "de248ad7b14cbd4e62207297826e21c2aaae36f4");
    assertHash(strSpec(), "5ac99f914f66deae94b7b0d990e821fe2117cf61");
    assertHash(recSpec(list(blobSpec())), "cd811460be1ebf123cab1361cfca1f49dd5c29c5");

    assertHash(arraySpec(blobSpec()), "7fbebe7b9e6730b6b49fbd19811677bbd1d8880b");
    assertHash(arraySpec(boolSpec()), "b2d929df4b382081405170f09fbe0febb32f547d");
    assertHash(arraySpec(definedLambdaSpec()), "35f599db587ed8b7b52bb0ce3a07a2d4d9c1c223");
    assertHash(arraySpec(intSpec()), "ff10a58462549d6f2a55c51be139ae52b62bf801");
    assertHash(arraySpec(nativeLambdaSpec()), "f86d7ed6e116f73533e856d5848e98ec04464f36");
    assertHash(arraySpec(nothingSpec()), "223052eec1e2f74ed0234fd1ba43f4d49e7e43db");
    assertHash(arraySpec(strSpec()), "dfeac8190688130683d51d719055d46c47cec4d2");
    assertHash(arraySpec(recSpec(list(blobSpec()))), "e843737d83eb150a51a81d9e43a00142982bd959");

    assertHash(callSpec(), "ff8d3400b13491d5877a50eb5c1b84511f9df3cf");
    assertHash(constSpec(), "127a54cf4fbd31a588a9cf45f63b37df7dc25f16");
    assertHash(eArraySpec(), "e52cb384593137a1d11ba4c390c62c4b6b365873");
    assertHash(fieldReadSpec(), "3af755b3a793a22c697141a8aa7598f1f0217262");
    assertHash(nullSpec(), "e43dd13c440350f8543c90ec6d90deb36dfdbedc");
    assertHash(refSpec(), "c0bdfdd54f44a37833c74da7613b87a5ba9a8452");
  }

  private static void assertHash(Spec spec, String hash) {
    assertThat(spec.hash())
        .isEqualTo(Hash.decode(hash));
  }
}
