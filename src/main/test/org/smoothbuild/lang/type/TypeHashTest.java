package org.smoothbuild.lang.type;

import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class TypeHashTest {
  @Test
  public void hashes_of_types_are_stable() throws Exception {
    TypeSystem typeSystem = new TypeSystem();
    assertHash(typeSystem.type(), "7b5ffbdc620f77f806320ba5562ccfcafae2214b");
    assertHash(typeSystem.string(), "7561a6b22d5fe8e18dec31904e0e9cdf6644ca96");
    assertHash(typeSystem.blob(), "6cb65ce7804fbcabff468d1d7aca46d4b5279f00");
    assertHash(typeSystem.nothing(), "9044640e5b343d11e66b356a82564b618d8df7e6");
    assertHash(typeSystem.array(typeSystem.type()), "a3000ef0acab0cdcb657fd32a0ffbc84242030a8");
    assertHash(typeSystem.array(typeSystem.string()), "e512d5a472c0a1f893f98e42f06477a1a0a1a675");
    assertHash(typeSystem.array(typeSystem.blob()), "a9e9aaa1450fee5c9a1a18a9c2cf1674e6ee611b");
    assertHash(typeSystem.array(typeSystem.nothing()), "5338d0bb9718388a329374e779726c6ed0a4d6d4");
    assertHash(structType(typeSystem), "a23d6de28f8161a221df3720bf89429ef9b4c62c");
  }

  private StructType structType(TypeSystem typeSystem) {
    return typeSystem.struct("NewType",
        ImmutableMap.of("name", typeSystem.string(), "data", typeSystem.blob()));
  }

  private void assertHash(Type type, String hash) {
    when(() -> type.hash());
    thenReturned(HashCode.fromString(hash));
  }
}
