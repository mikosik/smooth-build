package org.smoothbuild.acceptance.cmd;

import static org.smoothbuild.util.Strings.unlines;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public abstract class ShowTasksOptionTestCase extends AcceptanceTestCase {
  @Test
  public void illegal_show_tasks_value_causes_error() throws IOException {
    givenScript("result = 'abc';");
    whenSmoothCommandWithOption("--show-tasks=ILLEGAL");
    thenFinishedWithError();
    thenSysErrContains(unlines(
        "Invalid value for option '--show-tasks': Unknown matcher 'ILLEGAL'.",
        "",
        "Usage:"
    ));
  }

  protected abstract void whenSmoothCommandWithOption(String option);
}
