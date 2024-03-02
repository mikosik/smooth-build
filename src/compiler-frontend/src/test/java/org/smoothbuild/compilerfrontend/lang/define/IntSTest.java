package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.TestingExpressionS;

public class IntSTest extends TestingExpressionS {
  @Test
  public void to_string() {
    assertThat(intS(7, 16).toString()).isEqualTo("IntS(Int, 16, {prj}/build.smooth:7)");
  }
}
