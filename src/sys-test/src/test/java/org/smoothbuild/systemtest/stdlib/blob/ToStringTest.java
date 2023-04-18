package org.smoothbuild.systemtest.stdlib.blob;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestCase;

public class ToStringTest extends SystemTestCase {
  @Test
  public void to_string_func() throws IOException {
    createUserModule("""
            result = toString(0x41);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsString("result"))
        .isEqualTo("A");
  }
}
