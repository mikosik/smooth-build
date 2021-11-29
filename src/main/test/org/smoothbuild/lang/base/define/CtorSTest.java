package org.smoothbuild.lang.base.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class CtorSTest extends TestingContext {
  @Test
  public void to_string() {
    var func = ctorS(personST(), "myConstructor", param(intST(), "myParam"));
    assertThat(func.toString())
        .isEqualTo("CtorS(`Person myConstructor(Int myParam)`)");
  }
}
