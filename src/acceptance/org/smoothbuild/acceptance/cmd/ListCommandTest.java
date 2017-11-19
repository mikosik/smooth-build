package org.smoothbuild.acceptance.cmd;

import static org.hamcrest.Matchers.containsString;
import static org.testory.Testory.then;

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
    then(output(), containsString("aFunction\n"
        + "bFunction\n"
        + "cFunction\n"
        + "dFunction\n"));
  }
}
