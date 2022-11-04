package org.smoothbuild.compile.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class BlobSTest extends TestContext {
  @Test
  public void to_string() {
    assertThat(blobS(7, 16).toString())
        .isEqualTo("BlobS(Blob, 0x10, myBuild.smooth:7)");
  }
}
