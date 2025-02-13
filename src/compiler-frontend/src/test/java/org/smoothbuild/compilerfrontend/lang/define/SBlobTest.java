package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.type.STypeVarSet.sTypeVarSet;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SBlobTest extends FrontendCompilerTestContext {
  @Test
  void to_source_code() {
    assertThat(sBlob(16).toSourceCode(sTypeVarSet())).isEqualTo("0x10");
  }

  @Test
  void to_string() {
    assertThat(sBlob(7, 16).toString())
        .isEqualTo(
            """
        SBlob(
          type = Blob
          byteString = 0x10
          location = {t-project}/module.smooth:7
        )""");
  }
}
