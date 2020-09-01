package org.smoothbuild.acceptance.slib.bool;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class TrueTest extends AcceptanceTestCase {
  @Test
  public void true_function() throws IOException {
    createUserModule("""
            result = true;
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }
}
