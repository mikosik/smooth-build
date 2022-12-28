package org.smoothbuild.compile.fs.lang.define;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

import com.google.common.truth.Truth;

public class StringSTest extends TestContext {
  @Test
  public void to_string() {
    Truth.assertThat(stringS(7, "abc").toString())
        .isEqualTo("StringS(String, \"abc\", myBuild.smooth:7)");
  }
}
