package org.smoothbuild.lang.base.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.define.TestingModulePath.modulePath;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class MapFunctionSTest extends TestingContext {
  @Test
  public void to_string() {
    var function = new MapFunctionS(modulePath(), typeFactoryS());
    assertThat(function.toString())
        .isEqualTo("MapFunctionS(`[R] map([E] array,R(E) function)`)");
  }
}
