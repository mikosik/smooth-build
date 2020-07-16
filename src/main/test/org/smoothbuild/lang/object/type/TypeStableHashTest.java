package org.smoothbuild.lang.object.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.db.ObjectDb;
import org.smoothbuild.testing.TestingContext;

public class TypeStableHashTest extends TestingContext {
  @Test
  public void hashes_of_types_are_stable() {
    assertHash(typeType(), "14764e5894f9e586eb2fd50f0e0cf6d3b7f5df2b");
    assertHash(boolType(), "191329dd09662183b6ff84bfd0974bee63bfeaa7");
    assertHash(stringType(), "ebb7b4e331dbccdabf478446d50c94fe41c7aad1");
    assertHash(blobType(), "9d4ec666c8ffca9153ddd9b954a4c4a2a0626bd5");
    assertHash(nothingType(), "9d783ccc61946cc04a060718dd6cebc43e15faa9");
    assertHash(arrayType(typeType()), "09162b5575b7e9789ac39a3fcc38a489097bc589");
    assertHash(arrayType(boolType()), "f082a89aac074674527aee80f27b6b88e887b7f0");
    assertHash(arrayType(stringType()), "c8999ed960294d33405a2854370c6cbde15cdc02");
    assertHash(arrayType(blobType()), "02c7be1aa35535d48a7d132fa14fab80c02b1c10");
    assertHash(arrayType(nothingType()), "50ed8d7e2a595dd932606e3ddb3f080b0a852adb");
    assertHash(structType(objectDb()), "da349340eec7fba9b53d46faff52893f56c37947");
  }

  private TupleType structType(ObjectDb objectDb) {
    return objectDb.structType(list(objectDb.stringType(), objectDb.blobType()));
  }

  private static void assertHash(BinaryType type, String hash) {
    assertThat(type.hash())
        .isEqualTo(Hash.decode(hash));
  }
}
