package org.smoothbuild.acceptance.builtin.string;

import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ConcatenateTest extends AcceptanceTestCase {
  @Test
  public void concatenate_strings_function() throws Exception {
    givenBuildScript(script("result: concatenateStrings(strings=['abc'], with=['def']);"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", isArrayWith("abc", "def"));
  }
}
