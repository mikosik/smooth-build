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
    assertHash(blobSpec(), "8be891c7170d4d1bbca0ffcb3fce71285c55aee4");
    assertHash(boolSpec(), "cf1a0a6c0b2bd3fb9bc6dd67c6c2497cf94cdedf");
    assertHash(definedLambdaSpec(), "bff9c80955d696e11c3edbf79aa3f32859373f87");
    assertHash(intSpec(), "7ab8dc8456c25f132551f157c77a1888ef918fac");
    assertHash(nativeLambdaSpec(), "f41fcf2198f2d7f9d65802f0bb6c88bfd535ac68");
    assertHash(nothingSpec(), "0a2b2a825165ae9742c63b0c6ddafc22f0bd3b1e");
    assertHash(strSpec(), "de248ad7b14cbd4e62207297826e21c2aaae36f4");
    assertHash(recSpec(list(blobSpec())), "9bac03092234be3f7226004d38df9caa21228429");

    assertHash(arraySpec(blobSpec()), "8800c7594a868985fe01a2ab00a29083df788fc7");
    assertHash(arraySpec(boolSpec()), "4d42bef63f74197a8780495faf97f1e6c2b9cccd");
    assertHash(arraySpec(definedLambdaSpec()), "bbcb662abda9806fc1e6613fa2b882e1d58d364d");
    assertHash(arraySpec(intSpec()), "f1b3b9ee0591d24c0762b8ac6093f4e5be09e8da");
    assertHash(arraySpec(nativeLambdaSpec()), "c1bab51fc96d20087fcee22a7dff6e52ce2a7f63");
    assertHash(arraySpec(nothingSpec()), "3455dd4106f9763675c88becf9161eb31912cbe3");
    assertHash(arraySpec(strSpec()), "a4cdb35e5fac7a1152a3cd73a1b66fb960a8b4e9");
    assertHash(arraySpec(recSpec(list(blobSpec()))), "67fcafaffe0aea35d583b7425ed4e8556c519d7e");

    assertHash(callSpec(), "ff8d3400b13491d5877a50eb5c1b84511f9df3cf");
    assertHash(constSpec(), "8aacfde27b33a25ea9797815353fa580f744b3cb");
    assertHash(eArraySpec(), "127a54cf4fbd31a588a9cf45f63b37df7dc25f16");
    assertHash(fieldReadSpec(), "5ac99f914f66deae94b7b0d990e821fe2117cf61");
    assertHash(nullSpec(), "e52cb384593137a1d11ba4c390c62c4b6b365873");
    assertHash(refSpec(), "3af755b3a793a22c697141a8aa7598f1f0217262");
  }

  private static void assertHash(Spec spec, String hash) {
    assertThat(spec.hash())
        .isEqualTo(Hash.decode(hash));
  }
}
