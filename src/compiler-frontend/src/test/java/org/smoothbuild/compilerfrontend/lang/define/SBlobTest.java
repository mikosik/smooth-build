package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sBlob;

import org.junit.jupiter.api.Test;

public class SBlobTest {
  @Test
  public void to_string() {
    assertThat(sBlob(7, 16).toString()).isEqualTo("SBlob(Blob, 0x10, {project}/build.smooth:7)");
  }
}
