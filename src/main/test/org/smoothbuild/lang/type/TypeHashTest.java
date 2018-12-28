package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.base.Field;

import com.google.common.hash.HashCode;

public class TypeHashTest {
  @Test
  public void hashes_of_types_are_stable() throws Exception {
    TypesDb typesDb = new TestingTypesDb();
    assertHash(typesDb.type(), "7b5ffbdc620f77f806320ba5562ccfcafae2214b");
    assertHash(typesDb.bool(), "912e97481a6f232997c26729f48c14d33540c9e1");
    assertHash(typesDb.string(), "7561a6b22d5fe8e18dec31904e0e9cdf6644ca96");
    assertHash(typesDb.blob(), "6cb65ce7804fbcabff468d1d7aca46d4b5279f00");
    assertHash(typesDb.nothing(), "9044640e5b343d11e66b356a82564b618d8df7e6");
    assertHash(typesDb.array(typesDb.type()), "a3000ef0acab0cdcb657fd32a0ffbc84242030a8");
    assertHash(typesDb.array(typesDb.bool()), "aa0cbea2dccb4fbbd596df2be9ba5116cda382d9");
    assertHash(typesDb.array(typesDb.string()), "e512d5a472c0a1f893f98e42f06477a1a0a1a675");
    assertHash(typesDb.array(typesDb.blob()), "a9e9aaa1450fee5c9a1a18a9c2cf1674e6ee611b");
    assertHash(typesDb.array(typesDb.nothing()), "5338d0bb9718388a329374e779726c6ed0a4d6d4");
    assertHash(structType(typesDb), "a23d6de28f8161a221df3720bf89429ef9b4c62c");
  }

  private StructType structType(TypesDb typesDb) {
    return typesDb.struct("NewType", list(
        new Field(typesDb.string(), "name", unknownLocation()),
        new Field(typesDb.blob(), "data", unknownLocation())));
  }

  private void assertHash(ConcreteType type, String hash) {
    when(() -> type.hash());
    thenReturned(HashCode.fromString(hash));
  }
}
