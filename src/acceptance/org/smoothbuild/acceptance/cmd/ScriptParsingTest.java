package org.smoothbuild.acceptance.cmd;

import static org.hamcrest.Matchers.containsString;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ScriptParsingTest extends AcceptanceTestCase {
  @Test
  public void syntax_error_is_reported() throws Exception {
    givenScript("result = ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "build.smooth:1: error: no viable alternative at input '<EOF>'\n"));
  }
}
