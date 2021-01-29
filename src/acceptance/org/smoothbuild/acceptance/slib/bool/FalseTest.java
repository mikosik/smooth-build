package org.smoothbuild.acceptance.slib.bool;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class FalseTest extends AcceptanceTestCase {
  @Test
  public void false_value() throws IOException {
    createUserModule("""
            result = false;
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(false);

  }
}
