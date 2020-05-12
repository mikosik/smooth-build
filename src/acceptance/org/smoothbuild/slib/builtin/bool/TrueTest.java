package org.smoothbuild.slib.builtin.bool;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.slib.AcceptanceTestCase;

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
