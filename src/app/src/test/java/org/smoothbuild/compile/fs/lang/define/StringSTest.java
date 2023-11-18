package org.smoothbuild.compile.fs.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class StringSTest extends TestContext {
  @Test
  public void to_string() {
    assertThat(stringS(7, "abc").toString())
        .isEqualTo("StringS(String, \"abc\", build.smooth:7)");
  }
}
