package org.smoothbuild.acceptance.builtin.blob;

import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ConcatenateTest extends AcceptanceTestCase {
  @Test
  public void concatenate_blobs_function() throws Exception {
    givenFile("0", "abc");
    givenFile("1", "def");
    givenBuildScript(script("result: concatenateBlobs(blobs=[file('0')], with=[file('1')]);"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", isArrayWith("abc", "def"));
  }
}
