package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SIntTest extends FrontendCompilerTestContext {
  @Test
  void to_string() {
    assertThat(sInt(7, 16).toString()).isEqualTo("SInt(Int, 16, {t-project}/module.smooth:7)");
  }
}
