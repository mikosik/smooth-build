package org.smoothbuild.acceptance.builtin.bool;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class FalseTest extends AcceptanceTestCase {
  @Test
  public void false_function() throws IOException {
    givenScript(
        "  result = false();  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(false);

  }
}
