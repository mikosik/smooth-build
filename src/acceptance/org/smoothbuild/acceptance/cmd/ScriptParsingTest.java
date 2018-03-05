package org.smoothbuild.acceptance.cmd;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ScriptParsingTest extends AcceptanceTestCase {
  @Test
  public void syntax_error_is_reported() throws Exception {
    givenScript("result = ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(1, "no viable alternative at input '<EOF>'\n");
  }
}
