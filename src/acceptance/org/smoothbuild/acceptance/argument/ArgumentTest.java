package org.smoothbuild.acceptance.argument;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ArgumentTest extends AcceptanceTestCase {
  @Test
  public void trailing_comma_in_argument_list() throws IOException {
    givenBuildScript(script("result : toBlob(string='abc',) ;"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
  }
}
