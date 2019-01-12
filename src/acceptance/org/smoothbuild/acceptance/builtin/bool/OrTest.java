package org.smoothbuild.acceptance.builtin.bool;

import static org.testory.Testory.thenEqual;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

import okio.ByteString;

public class OrTest extends AcceptanceTestCase {

  @Test
  public void false_or_false_returns_false() throws IOException {
    givenScript("result = or(false(), false());");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactAsByteStrings("result"), ByteString.of((byte) 0));
  }

  @Test
  public void false_or_true_returns_true() throws IOException {
    givenScript("result = or(false(), true());");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactAsByteStrings("result"), ByteString.of((byte) 1));
  }

  @Test
  public void true_or_false_returns_true() throws IOException {
    givenScript("result = or(true(), false());");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactAsByteStrings("result"), ByteString.of((byte) 1));
  }

  @Test
  public void true_or_true_returns_true() throws IOException {
    givenScript("result = or(true(), true());");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactAsByteStrings("result"), ByteString.of((byte) 1));
  }
}
