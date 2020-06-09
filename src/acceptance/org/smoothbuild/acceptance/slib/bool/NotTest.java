package org.smoothbuild.acceptance.slib.bool;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class NotTest extends AcceptanceTestCase {

  @Test
  public void not_false_returns_true() throws IOException {
    createUserModule(
        "  result = not(false());  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }

  @Test
  public void not_true_returns_false() throws IOException {
    createUserModule(
        "  result = not(true());  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(false);
  }
}

