package org.smoothbuild.acceptance.builtin.blob;

import static org.junit.Assert.assertEquals;
import static org.smoothbuild.util.Lists.list;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.util.Lists;

public class ConcatenateBlobArraysTest extends AcceptanceTestCase {
  @Test
  public void concatenate_blob_arrays_function() throws Exception {
    givenFile("0", "abc");
    givenFile("1", "def");
    givenScript("result = concatenateBlobArrays(blobs=[file('//0')], with=[file('//1')]);");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertEquals(list("abc", "def"), artifactArray("result"));
  }
}
