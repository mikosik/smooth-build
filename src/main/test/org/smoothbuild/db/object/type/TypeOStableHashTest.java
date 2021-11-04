package org.smoothbuild.db.object.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Named.named;
import static org.smoothbuild.util.collect.NamedList.namedList;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeO;
import org.smoothbuild.testing.TestingContext;

public class TypeOStableHashTest extends TestingContext {
  @Test
  public void hashes_of_types_are_stable() {
    assertHash(anyOT(), "b35d79d5718f7bba2cda55c29e2408c13ffc8cd5");
    assertHash(blobOT(), "0a2b2a825165ae9742c63b0c6ddafc22f0bd3b1e");
    assertHash(boolOT(), "47f9cc533a5f0c6f650ff0528c0d54d6d2d9d9ab");
    assertHash(lambdaOT(), "72f4e3d7262b25cf65222b035b02e520c32f7a30");
    assertHash(intOT(), "8be891c7170d4d1bbca0ffcb3fce71285c55aee4");
    assertHash(nothingOT(), "de248ad7b14cbd4e62207297826e21c2aaae36f4");
    assertHash(tupleOT(list(blobOT())), "cd811460be1ebf123cab1361cfca1f49dd5c29c5");
    assertHash(stringOT(), "5ac99f914f66deae94b7b0d990e821fe2117cf61");
    assertHash(structOT("MyStruct", namedList(list(named("field", intOT())))),
        "8a4a8dba1b7ae414b16fb6a75889d93bd92cab6f");
    assertHash(variableOT("A"), "99f61661c131c5db3723bdaa5ad26229d06dc6ce");

    assertHash(arrayOT(anyOT()), "2dfdcb5ccf6df3057cb84565af5b67f64c685e9c");
    assertHash(arrayOT(blobOT()), "7fbebe7b9e6730b6b49fbd19811677bbd1d8880b");
    assertHash(arrayOT(boolOT()), "b2d929df4b382081405170f09fbe0febb32f547d");
    assertHash(arrayOT(lambdaOT()), "35f599db587ed8b7b52bb0ce3a07a2d4d9c1c223");
    assertHash(arrayOT(intOT()), "ff10a58462549d6f2a55c51be139ae52b62bf801");
    assertHash(arrayOT(nothingOT()), "223052eec1e2f74ed0234fd1ba43f4d49e7e43db");
    assertHash(arrayOT(stringOT()), "dfeac8190688130683d51d719055d46c47cec4d2");
    assertHash(arrayOT(tupleOT(list(blobOT()))), "e843737d83eb150a51a81d9e43a00142982bd959");
    assertHash(arrayOT(variableOT("A")), "5eae2a05ffe2fee34c07a74779e5eecde3521849");

    assertHash(orderOT(intOT()), "04c4786e96bdac890b5089eee99173cd41cecb33");
    assertHash(callOT(intOT()), "f8e8b1d061fcdfb8be52b97cf80e50bc908e59ef");
    assertHash(constOT(intOT()), "dd2a014b3764aa9ab8875ff924449ee241cb5e7c");
    assertHash(nativeMethodOT(), "7ed79e11d37cd84bc98404285db5eb584037a520");
    assertHash(refOT(intOT()), "fca4f7c553d3189b050352933410a5b8b6ceadb6");
    assertHash(selectOT(intOT()), "bc511a321ba8a722155574c39f0cb5f58ee84a71");
  }

  private static void assertHash(TypeO type, String hash) {
    assertThat(type.hash())
        .isEqualTo(Hash.decode(hash));
  }
}
