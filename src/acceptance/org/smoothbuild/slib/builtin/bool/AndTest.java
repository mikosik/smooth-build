package org.smoothbuild.slib.builtin.bool;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.slib.AcceptanceTestCase;
import org.smoothbuild.slib.testing.ThrowException;

public class AndTest extends AcceptanceTestCase {
  @Test
  public void false_and_false_returns_false() throws IOException {
    givenScript(
        "  result = and(false(), false());  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(false);
  }

  @Test
  public void false_and_true_returns_false() throws IOException {
    givenScript(
        "  result = and(false(), true());  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(false);
  }

  @Test
  public void true_and_false_returns_false() throws IOException {
    givenScript(
        "  result = and(true(), false());  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(false);
  }

  @Test
  public void true_and_true_returns_true() throws IOException {
    givenScript(
        "  result = and(true(), true());  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }

  @Test
  public void second_value_should_not_be_evaluated_when_first_is_false() throws Exception {
    givenNativeJar(ThrowException.class);
    givenScript(
        "  Nothing throwException();                 ",
        "  result = and(false(), throwException());  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(false);
  }
}
