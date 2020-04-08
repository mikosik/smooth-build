package org.smoothbuild.acceptance.cmd;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ListCommandTest extends AcceptanceTestCase {
  @Test
  public void list_command_lists_all_available_functions() throws Exception {
    givenScript(
        "  bFunction = 'abc';  ",
        "  aFunction = 'abc';  ",
        "  dFunction = 'abc';  ",
        "  cFunction = 'abc';  ");
    whenSmoothList();
    thenFinishedWithSuccess();
    thenOutputContains(
        "aFunction",
        "bFunction",
        "cFunction",
        "dFunction",
        "");
  }

  @Test
  public void fails_when_script_file_is_missing() {
    whenSmoothList();
    thenFinishedWithError();
    thenOutputContains("error: 'build.smooth' doesn't exist.\n");
  }
}
