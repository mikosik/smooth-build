package org.smoothbuild.acceptance.builtin.string;

import static org.junit.Assert.assertEquals;
import static org.smoothbuild.util.Lists.list;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ConcatenateStringArraysTest extends AcceptanceTestCase {
  @Test
  public void concatenate_string_arrays_function() throws Exception {
    givenScript("result = concatenateStringArrays(strings=['abc'], with=['def']);");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertEquals(list("abc", "def"), artifactArray("result"));
  }
}
