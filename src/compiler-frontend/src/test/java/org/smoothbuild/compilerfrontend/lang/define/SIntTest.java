package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sInt;

import org.junit.jupiter.api.Test;

public class SIntTest {
  @Test
  void to_string() {
    assertThat(sInt(7, 16).toString()).isEqualTo("SInt(Int, 16, {t-project}/build.smooth:7)");
  }
}
