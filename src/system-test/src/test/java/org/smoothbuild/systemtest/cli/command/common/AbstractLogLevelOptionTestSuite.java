package org.smoothbuild.systemtest.cli.command.common;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestContext;

public abstract class AbstractLogLevelOptionTestSuite extends SystemTestContext {
  @Test
  void illegal_log_level_value_causes_error() throws IOException {
    createUserModule("""
            result = "abc";
            """);
    whenSmoothCommandWithOption("--log-level=wrong_value");
    assertFinishedWithError();
    assertSystemErrContains(
        """
        Invalid value for option '--log-level': expected one of {f,fatal,e,error,w,warning,i,info} (case-sensitive) but was 'wrong_value'

        Usage:""");
  }

  protected abstract void whenSmoothCommandWithOption(String option);
}
