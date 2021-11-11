package org.smoothbuild.lang.base.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class DefinedFunctionTest extends TestingContext {
  @Test
  public void to_string() {
    var function = functionS(stringST(), "myFunction", intS(17), param(intST(), "myParam"));
    assertThat(function.toString())
        .isEqualTo("DefinedFunction(`String myFunction(Int myParam) = ?`)");
  }
}
