package org.smoothbuild.acceptance.cmd;

import static org.testory.Testory.thenEqual;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ScriptParsingTest extends AcceptanceTestCase {
  @Test
  public void syntax_error_is_reported() throws Exception {
    givenScript("result: ");
    whenSmoothBuild();
    thenFinishedWithError();
    thenEqual(output(), "build.smooth:1: error: no viable alternative at input '<EOF>'\n");
  }
}
