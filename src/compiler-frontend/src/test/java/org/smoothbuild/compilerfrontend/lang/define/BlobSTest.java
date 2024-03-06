package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.blobS;

import org.junit.jupiter.api.Test;

public class BlobSTest {
  @Test
  public void to_string() {
    assertThat(blobS(7, 16).toString()).isEqualTo("BlobS(Blob, 0x10, {prj}/build.smooth:7)");
  }
}
