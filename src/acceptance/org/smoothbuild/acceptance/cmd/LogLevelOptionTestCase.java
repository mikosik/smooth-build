package org.smoothbuild.acceptance.cmd;

import static org.smoothbuild.util.Strings.unlines;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public abstract class LogLevelOptionTestCase extends AcceptanceTestCase {
  @Test
  public void illegal_log_level_value_causes_error() throws IOException {
    givenScript("result = 'abc';");
    whenSmoothCommandWithOption("--log-level=wrong_value");
    thenFinishedWithError();
    thenSysErrContains(unlines(
        "Invalid value for option '--log-level': expected one of " +
            "{fatal,f,error,e,warning,w,info,i} (case-sensitive) but was 'wrong_value'",
        "",
        "Usage:"
    ));
  }

  protected abstract void whenSmoothCommandWithOption(String option);
}
