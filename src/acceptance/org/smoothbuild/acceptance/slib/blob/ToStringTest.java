package org.smoothbuild.acceptance.slib.blob;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ToStringTest extends AcceptanceTestCase {
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
