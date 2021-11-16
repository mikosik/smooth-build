package org.smoothbuild.lang.base.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class NativeFunctionSTest extends TestingContext {
  @Test
  public void to_string() {
    var function = functionS(stringST(), "myFunction", param(intST(), "myParam"));
    assertThat(function.toString())
        .isEqualTo("NativeFunction(`@Native(\"Impl.met\", PURE) String myFunction(Int myParam)`)");
  }
}
