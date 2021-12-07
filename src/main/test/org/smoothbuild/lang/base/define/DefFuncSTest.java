package org.smoothbuild.lang.base.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class DefFuncSTest extends TestingContext {
  @Test
  public void to_string() {
    var func = defFuncS(stringTS(), "myFunc", intS(17), itemS(intTS(), "myParam"));
    assertThat(func.toString())
        .isEqualTo("DefFunc(`String myFunc(Int myParam) = ?`)");
  }
}
