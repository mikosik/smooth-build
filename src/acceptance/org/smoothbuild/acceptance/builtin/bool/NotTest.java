package org.smoothbuild.acceptance.builtin.bool;

import static org.testory.Testory.thenEqual;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class NotTest extends AcceptanceTestCase {

  @Test
  public void not_false_returns_true() throws IOException {
    givenScript(
        "  result = not(false());  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactAsBoolean("result"), true);
  }

  @Test
  public void not_true_returns_false() throws IOException {
    givenScript(
        "  result = not(true());  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactAsBoolean("result"), false);
  }
}

