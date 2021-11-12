package org.smoothbuild.lang.base.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.define.TestingModulePath.modulePath;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class IfFunctionSTest extends TestingContext {
  @Test
  public void to_string() {
    var function = new IfFunctionS(modulePath(), typeFactoryS());
    assertThat(function.toString())
        .isEqualTo("IfFunctionS(`A if(Bool condition,A then,A else)`)");
  }
}
