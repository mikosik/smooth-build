package org.smoothbuild.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.NList.nlist;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class DefFuncSTest extends TestingContext {
  @Test
  public void to_string() {
    var func = defFuncS(stringTS(), "myFunc", intS(17), nlist(itemS(intTS(), "myParam")));
    assertThat(func.toString())
        .isEqualTo("String myFunc(Int myParam) = ?");
  }
}
