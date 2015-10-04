package org.smoothbuild.acceptance.builtin.blob;

import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ConcatenateBlobArraysTest extends AcceptanceTestCase {
  @Test
  public void concatenate_blob_arrays_function() throws Exception {
    givenFile("0", "abc");
    givenFile("1", "def");
    givenScript("result: concatenateBlobArrays(blobs=[file('0')], with=[file('1')]);");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith("abc", "def"));
  }
}
