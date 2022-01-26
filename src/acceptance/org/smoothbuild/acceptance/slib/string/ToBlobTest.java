package org.smoothbuild.acceptance.slib.string;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ToBlobTest extends AcceptanceTestCase {
  @Test
  public void to_blob_func() throws IOException {
    createFile("file.txt", "abc");
    createUserModule("""
            result = toBlob("abc");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsString("result"))
        .isEqualTo("abc");
  }
}
