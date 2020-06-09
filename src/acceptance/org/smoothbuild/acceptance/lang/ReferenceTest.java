package org.smoothbuild.acceptance.lang;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ReferenceTest extends AcceptanceTestCase {
  @Test
  public void unknown_reference_causes_error() throws Exception {
    createUserModule(
        "  func(String existingParam) = unknown;  ",
        "  result = 'def';                        ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(1, "'unknown' is undefined.\n");
  }
}
