package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.TestingExpressionS;

public class BlobSTest extends TestingExpressionS {
  @Test
  public void to_string() {
    assertThat(blobS(7, 16).toString()).isEqualTo("BlobS(Blob, 0x10, {prj}/build.smooth:7)");
  }
}
