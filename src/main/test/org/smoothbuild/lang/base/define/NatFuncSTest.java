package org.smoothbuild.lang.base.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class NatFuncSTest extends TestingContext {
  @Test
  public void to_string() {
    var func = natFuncS(stringTS(), "myFunc", itemS(intTS(), "myParam"));
    assertThat(func.toString())
        .isEqualTo("NatFunc(`@Native(\"Impl.met\", PURE) String myFunc(Int myParam)`)");
  }
}
