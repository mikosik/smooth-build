package org.smoothbuild.systemtest.stdlib.common;

import static com.google.common.truth.Truth.assertThat;

import okio.ByteString;
import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestCase;

public class IdTest extends SystemTestCase {
  @Test
  public void returns_unchanged_string_value() throws Exception {
    createUserModule("""
            result = id("abc");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsString("result")).isEqualTo("abc");
  }

  @Test
  public void returns_unchanged_blob_value() throws Exception {
    createUserModule("""
            result = id(0x010203);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsByteString("result"))
        .isEqualTo(ByteString.of((byte) 1, (byte) 2, (byte) 3));
  }
}
