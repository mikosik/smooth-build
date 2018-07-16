package org.smoothbuild.acceptance.cmd;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class DagCommandTest extends AcceptanceTestCase {

  @Test
  public void dag() throws Exception {
    givenScript("mySingleton(String element) = [element, 'def'];        \n"
        + "      result = mySingleton('abc');                           \n");
    whenSmoothDag("result");
    thenFinishedWithSuccess();
    thenOutputContains("result([String])\n" +
        "  mySingleton([String])\n" +
        "    [String]([String])\n" +
        "      String(String)\n" +
        "      String(String)");
  }
}
