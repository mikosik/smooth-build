package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SBlobTest extends FrontendCompilerTestContext {
  @Test
  void to_string() {
    assertThat(sBlob(7, 16).toString()).isEqualTo("SBlob(Blob, 0x10, {t-project}/module.smooth:7)");
  }
}
