package org.smoothbuild.acceptance.builtin.string;

import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ConcatenateStringArraysTest extends AcceptanceTestCase {
  @Test
  public void concatenate_string_arrays_function() throws Exception {
    givenScript("result: concatenateStringArrays(strings=['abc'], with=['def']);");
    whenSmoothBuild("result");
    thenReturnedCode(0);
    then(artifact("result"), isArrayWith("abc", "def"));
  }
}
