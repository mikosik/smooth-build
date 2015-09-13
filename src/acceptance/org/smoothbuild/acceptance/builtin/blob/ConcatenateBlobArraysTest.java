package org.smoothbuild.acceptance.builtin.blob;

import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ConcatenateBlobArraysTest extends AcceptanceTestCase {
  @Test
  public void concatenate_blob_arrays_function() throws Exception {
    givenFile("0", "abc");
    givenFile("1", "def");
    givenBuildScript(script("result: concatenateBlobArrays(blobs=[file('0')], with=[file('1')]);"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", isArrayWith("abc", "def"));
  }
}
