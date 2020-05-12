package org.smoothbuild.slib.lang;

import org.junit.jupiter.api.Test;
import org.smoothbuild.slib.AcceptanceTestCase;

public class ReferenceTest extends AcceptanceTestCase {
  @Test
  public void unknown_reference_causes_error() throws Exception {
    givenScript(
        "  func(String existingParam) = unknown;  ",
        "  result = 'def';                        ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContainsParseError(1, "'unknown' is undefined.\n");
  }
}
