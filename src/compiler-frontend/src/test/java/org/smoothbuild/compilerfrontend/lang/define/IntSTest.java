package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.intS;

import org.junit.jupiter.api.Test;

public class IntSTest {
  @Test
  public void to_string() {
    assertThat(intS(7, 16).toString()).isEqualTo("IntS(Int, 16, {prj}/build.smooth:7)");
  }
}
