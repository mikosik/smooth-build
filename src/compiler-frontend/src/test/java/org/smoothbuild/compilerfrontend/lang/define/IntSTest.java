package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.TestExpressionS;

public class IntSTest extends TestExpressionS {
  @Test
  public void to_string() {
    assertThat(intS(7, 16).toString()).isEqualTo("IntS(Int, 16, {prj}/build.smooth:7)");
  }
}
