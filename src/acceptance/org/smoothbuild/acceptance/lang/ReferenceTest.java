package org.smoothbuild.acceptance.lang;

import static org.hamcrest.Matchers.containsString;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ReferenceTest extends AcceptanceTestCase {
  @Test
  public void unknown_reference_causes_error() throws Exception {
    givenScript("func(String existingParam) = unknown;"
        + "result = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("build.smooth:1: error: 'unknown' is undefined.\n"));
  }
}
