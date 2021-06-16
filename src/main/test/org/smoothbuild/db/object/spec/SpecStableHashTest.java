package org.smoothbuild.db.object.spec;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContext;

public class SpecStableHashTest extends TestingContext {
  @Test
  public void hashes_of_specs_are_stable() {
    assertHash(anySpec(), "8aacfde27b33a25ea9797815353fa580f744b3cb");
    assertHash(blobSpec(), "8be891c7170d4d1bbca0ffcb3fce71285c55aee4");
    assertHash(boolSpec(), "cf1a0a6c0b2bd3fb9bc6dd67c6c2497cf94cdedf");
    assertHash(nothingSpec(), "0a2b2a825165ae9742c63b0c6ddafc22f0bd3b1e");
    assertHash(stringSpec(), "de248ad7b14cbd4e62207297826e21c2aaae36f4");
    assertHash(tupleSpec(list(blobSpec())), "9bac03092234be3f7226004d38df9caa21228429");

    assertHash(arraySpec(anySpec()), "148c399a51dd9352970406e4e13b14b5c1c275d2");
    assertHash(arraySpec(blobSpec()), "8800c7594a868985fe01a2ab00a29083df788fc7");
    assertHash(arraySpec(boolSpec()), "4d42bef63f74197a8780495faf97f1e6c2b9cccd");
    assertHash(arraySpec(nothingSpec()), "3455dd4106f9763675c88becf9161eb31912cbe3");
    assertHash(arraySpec(stringSpec()), "a4cdb35e5fac7a1152a3cd73a1b66fb960a8b4e9");
    assertHash(arraySpec(tupleSpec(list(blobSpec()))), "67fcafaffe0aea35d583b7425ed4e8556c519d7e");
  }

  private static void assertHash(Spec spec, String hash) {
    assertThat(spec.hash())
        .isEqualTo(Hash.decode(hash));
  }
}
