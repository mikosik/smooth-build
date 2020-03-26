package org.smoothbuild.acceptance.builtin.bool;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.ThrowException;

public class OrTest extends AcceptanceTestCase {

  @Test
  public void false_or_false_returns_false() throws IOException {
    givenScript(
        "  result = or(false(), false());  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(false);
  }

  @Test
  public void false_or_true_returns_true() throws IOException {
    givenScript(
        "  result = or(false(), true());  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }

  @Test
  public void true_or_false_returns_true() throws IOException {
    givenScript(
        "  result = or(true(), false());  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }

  @Test
  public void true_or_true_returns_true() throws IOException {
    givenScript(
        "  result = or(true(), true());  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }

  @Test
  public void second_value_should_not_be_evaluated_when_first_is_true() throws Exception {
    givenNativeJar(ThrowException.class);
    givenScript(
        "  Nothing throwException();               ",
        "  result = or(true(), throwException());  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }
}
