package org.smoothbuild.acceptance.builtin.bool;

import static org.testory.Testory.thenEqual;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

import okio.ByteString;

public class TrueTest extends AcceptanceTestCase {
  @Test
  public void true_function() throws IOException {
    givenScript("result = true();");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactAsByteStrings("result"), ByteString.of((byte) 1));
  }
}
