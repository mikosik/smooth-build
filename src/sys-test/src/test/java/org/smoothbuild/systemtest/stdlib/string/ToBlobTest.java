package org.smoothbuild.systemtest.stdlib.string;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestCase;

public class ToBlobTest extends SystemTestCase {
  @Test
  public void to_blob_func() throws IOException {
    createFile("file.txt", "abc");
    createUserModule("""
            result = toBlob("abc");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsString("result")).isEqualTo("abc");
  }
}
