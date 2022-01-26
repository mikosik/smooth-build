package org.smoothbuild.lang.base.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.define.TestingModPath.modPath;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class IfFuncSTest extends TestingContext {
  @Test
  public void to_string() {
    var func = new IfFuncS(modPath(), typeSF());
    assertThat(func.toString())
        .isEqualTo("IfFuncS(`A if(Bool condition,A then,A else)`)");
  }
}
