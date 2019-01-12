package org.smoothbuild.acceptance.builtin.bool;

import static org.testory.Testory.thenEqual;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

import okio.ByteString;

public class FalseTest extends AcceptanceTestCase {
  @Test
  public void false_function() throws IOException {
    givenScript("result = false();");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactAsBoolean("result"), false);
  }
}