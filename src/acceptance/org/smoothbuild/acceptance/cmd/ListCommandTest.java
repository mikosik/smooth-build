package org.smoothbuild.acceptance.cmd;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ListCommandTest extends AcceptanceTestCase {
  @Test
  public void list_command_lists_all_available_functions() throws Exception {
    givenScript("bFunction = 'abc';"
        + "      aFunction = 'abc';"
        + "      dFunction = 'abc';"
        + "      cFunction = 'abc';");
    whenSmoothList();
    thenFinishedWithSuccess();
    thenOutputContains("aFunction\n"
        + "bFunction\n"
        + "cFunction\n"
        + "dFunction\n");
  }
}
