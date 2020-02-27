package org.smoothbuild.acceptance.lang;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ReferenceTest extends AcceptanceTestCase {
  @Test
  public void unknown_reference_causes_error() throws Exception {
    givenScript(
        "  func(String existingParam) = unknown;  ",
        "  result = 'def';                        ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(1, "'unknown' is undefined.\n");
  }
}
