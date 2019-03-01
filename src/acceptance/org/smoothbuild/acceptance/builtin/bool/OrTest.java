package org.smoothbuild.acceptance.builtin.bool;

import static org.hamcrest.Matchers.equalTo;
import static org.testory.Testory.then;
import static org.testory.Testory.thenEqual;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.ThrowException;

public class OrTest extends AcceptanceTestCase {

  @Test
  public void false_or_false_returns_false() throws IOException {
    givenScript("result = or(false(), false());");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactAsBoolean("result"), false);
  }

  @Test
  public void false_or_true_returns_true() throws IOException {
    givenScript("result = or(false(), true());");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactAsBoolean("result"), true);
  }

  @Test
  public void true_or_false_returns_true() throws IOException {
    givenScript("result = or(true(), false());");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactAsBoolean("result"), true);
  }

  @Test
  public void true_or_true_returns_true() throws IOException {
    givenScript("result = or(true(), true());");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactAsBoolean("result"), true);
  }

  @Test
  public void second_value_should_not_be_evaluated_when_first_is_true() throws Exception {
    givenNativeJar(ThrowException.class);
    givenScript("Nothing throwException();" +
        "        result = or(true(), throwException());");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactAsBoolean("result"), equalTo(true));
  }
}
