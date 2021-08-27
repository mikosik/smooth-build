package org.smoothbuild.db.object.spec;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContext;

public class SpecStableHashTest extends TestingContext {
  @Test
  public void hashes_of_specs_are_stable() {
    assertHash(blobS(), "8be891c7170d4d1bbca0ffcb3fce71285c55aee4");
    assertHash(boolS(), "cf1a0a6c0b2bd3fb9bc6dd67c6c2497cf94cdedf");
    assertHash(intS(), "7ab8dc8456c25f132551f157c77a1888ef918fac");
    assertHash(nothingS(), "0a2b2a825165ae9742c63b0c6ddafc22f0bd3b1e");
    assertHash(strS(), "de248ad7b14cbd4e62207297826e21c2aaae36f4");
    assertHash(tupleS(list(blobS())), "9bac03092234be3f7226004d38df9caa21228429");
    assertHash(constS(), "8aacfde27b33a25ea9797815353fa580f744b3cb");
    assertHash(fieldReadS(), "5ac99f914f66deae94b7b0d990e821fe2117cf61");
    assertHash(callS(), "ff8d3400b13491d5877a50eb5c1b84511f9df3cf");
    assertHash(eArrayS(), "127a54cf4fbd31a588a9cf45f63b37df7dc25f16");

    assertHash(arrayS(blobS()), "8800c7594a868985fe01a2ab00a29083df788fc7");
    assertHash(arrayS(boolS()), "4d42bef63f74197a8780495faf97f1e6c2b9cccd");
    assertHash(arrayS(intS()), "f1b3b9ee0591d24c0762b8ac6093f4e5be09e8da");
    assertHash(arrayS(nothingS()), "3455dd4106f9763675c88becf9161eb31912cbe3");
    assertHash(arrayS(strS()), "a4cdb35e5fac7a1152a3cd73a1b66fb960a8b4e9");
    assertHash(arrayS(tupleS(list(blobS()))), "67fcafaffe0aea35d583b7425ed4e8556c519d7e");
  }

  private static void assertHash(Spec spec, String hash) {
    assertThat(spec.hash())
        .isEqualTo(Hash.decode(hash));
  }
}
