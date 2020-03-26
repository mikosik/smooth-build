package org.smoothbuild.acceptance.builtin.bool;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class TrueTest extends AcceptanceTestCase {
  @Test
  public void true_function() throws IOException {
    givenScript(
        "  result = true();  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }
}
