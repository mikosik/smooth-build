package org.smoothbuild.acceptance.builtin.bool;

import static org.hamcrest.Matchers.equalTo;
import static org.testory.Testory.then;
import static org.testory.Testory.thenEqual;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.ThrowException;

public class AndTest extends AcceptanceTestCase {
  @Test
  public void false_and_false_returns_false() throws IOException {
    givenScript(
        "  result = and(false(), false());  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactAsBoolean("result"), false);
  }

  @Test
  public void false_and_true_returns_false() throws IOException {
    givenScript(
        "  result = and(false(), true());  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactAsBoolean("result"), false);
  }

  @Test
  public void true_and_false_returns_false() throws IOException {
    givenScript(
        "  result = and(true(), false());  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactAsBoolean("result"), false);
  }

  @Test
  public void true_and_true_returns_true() throws IOException {
    givenScript(
        "  result = and(true(), true());  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactAsBoolean("result"), true);
  }

  @Test
  public void second_value_should_not_be_evaluated_when_first_is_false() throws Exception {
    givenNativeJar(ThrowException.class);
    givenScript(
        "  Nothing throwException();                 ",
        "  result = and(false(), throwException());  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactAsBoolean("result"), equalTo(false));
  }
}
