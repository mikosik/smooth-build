package org.smoothbuild.compile.fs.lang.define;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

import com.google.common.truth.Truth;

public class IntSTest extends TestContext {
  @Test
  public void to_string() {
    Truth.assertThat(intS(7, 16).toString())
        .isEqualTo("IntS(Int, 16, myBuild.smooth:7)");
  }
}
