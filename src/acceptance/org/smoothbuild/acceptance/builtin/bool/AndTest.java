package org.smoothbuild.acceptance.builtin.bool;

import static org.testory.Testory.thenEqual;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

import okio.ByteString;

public class AndTest extends AcceptanceTestCase {
  @Test
  public void false_and_false_returns_false() throws IOException {
    givenScript("result = and(false(), false());");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactAsByteStrings("result"), ByteString.of((byte) 0));
  }

  @Test
  public void false_and_true_returns_false() throws IOException {
    givenScript("result = and(false(), true());");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactAsByteStrings("result"), ByteString.of((byte) 0));
  }

  @Test
  public void true_and_false_returns_false() throws IOException {
    givenScript("result = and(true(), false());");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactAsByteStrings("result"), ByteString.of((byte) 0));
  }

  @Test
  public void true_and_true_returns_true() throws IOException {
    givenScript("result = and(true(), true());");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactAsByteStrings("result"), ByteString.of((byte) 1));
  }
}
