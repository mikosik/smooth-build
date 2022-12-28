package org.smoothbuild.compile.fs.lang.define;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

import com.google.common.truth.Truth;

public class BlobSTest extends TestContext {
  @Test
  public void to_string() {
    Truth.assertThat(blobS(7, 16).toString())
        .isEqualTo("BlobS(Blob, 0x10, myBuild.smooth:7)");
  }
}
