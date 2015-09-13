package org.smoothbuild.acceptance.builtin.string;

import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ConcatenateStringArraysTest extends AcceptanceTestCase {
  @Test
  public void concatenate_string_arrays_function() throws Exception {
    givenBuildScript(script("result: concatenateStringArrays(strings=['abc'], with=['def']);"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", isArrayWith("abc", "def"));
  }
}
