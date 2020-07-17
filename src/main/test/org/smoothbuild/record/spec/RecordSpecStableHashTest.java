package org.smoothbuild.record.spec;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.record.db.RecordDb;
import org.smoothbuild.testing.TestingContext;

public class RecordSpecStableHashTest extends TestingContext {
  @Test
  public void hashes_of_specs_are_stable() {
    assertHash(specSpec(), "14764e5894f9e586eb2fd50f0e0cf6d3b7f5df2b");
    assertHash(boolSpec(), "191329dd09662183b6ff84bfd0974bee63bfeaa7");
    assertHash(stringSpec(), "ebb7b4e331dbccdabf478446d50c94fe41c7aad1");
    assertHash(blobSpec(), "9d4ec666c8ffca9153ddd9b954a4c4a2a0626bd5");
    assertHash(nothingSpec(), "9d783ccc61946cc04a060718dd6cebc43e15faa9");
    assertHash(arraySpec(specSpec()), "09162b5575b7e9789ac39a3fcc38a489097bc589");
    assertHash(arraySpec(boolSpec()), "f082a89aac074674527aee80f27b6b88e887b7f0");
    assertHash(arraySpec(stringSpec()), "c8999ed960294d33405a2854370c6cbde15cdc02");
    assertHash(arraySpec(blobSpec()), "02c7be1aa35535d48a7d132fa14fab80c02b1c10");
    assertHash(arraySpec(nothingSpec()), "50ed8d7e2a595dd932606e3ddb3f080b0a852adb");
    assertHash(tupleSpec(recordDb()), "da349340eec7fba9b53d46faff52893f56c37947");
  }

  private TupleSpec tupleSpec(RecordDb recordDb) {
    return recordDb.tupleSpec(list(recordDb.stringSpec(), recordDb.blobSpec()));
  }

  private static void assertHash(Spec spec, String hash) {
    assertThat(spec.hash())
        .isEqualTo(Hash.decode(hash));
  }
}
