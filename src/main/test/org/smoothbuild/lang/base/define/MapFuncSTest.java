package org.smoothbuild.lang.base.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.define.TestingModPath.modPath;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class MapFuncSTest extends TestingContext {
  @Test
  public void to_string() {
    var func = new MapFuncS(modPath(), typeFactoryS());
    assertThat(func.toString())
        .isEqualTo("MapFuncS(`[R] map([E] array,R(E) func)`)");
  }
}
