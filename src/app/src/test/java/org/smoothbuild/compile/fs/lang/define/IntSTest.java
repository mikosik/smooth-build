package org.smoothbuild.compile.fs.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class IntSTest extends TestContext {
  @Test
  public void to_string() {
    assertThat(intS(7, 16).toString())
        .isEqualTo("IntS(Int, 16, build.smooth:7)");
  }
}
