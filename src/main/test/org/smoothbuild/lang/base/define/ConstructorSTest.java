package org.smoothbuild.lang.base.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class ConstructorSTest extends TestingContext {
  @Test
  public void to_string() {
    var function = constructorS(personST(), "myConstructor", param(intST(), "myParam"));
    assertThat(function.toString())
        .isEqualTo("ConstructorS(`Person myConstructor(Int myParam)`)");
  }
}
