package org.smoothbuild.slib.cli.command;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;

import java.io.File;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.slib.AcceptanceTestCase;
import org.smoothbuild.slib.cli.command.common.LogLevelOptionTestCase;

@SuppressWarnings("ClassCanBeStatic")
public class CleanCommandTest {
  @Nested
  class basic extends AcceptanceTestCase {
    @Test
    public void clean_command_deletes_smooth_dir() throws Exception {
      givenScript(
          "  result = 'abc';  ");
      whenSmoothBuild("result");
      thenFinishedWithSuccess();
      whenSmoothClean();
      thenFinishedWithSuccess();
      assertThat(new File(projectDir(), SMOOTH_DIR.toString()).exists())
          .isFalse();
    }

    @Test
    public void clean_command_with_arguments_prints_error() throws Exception {
      givenScript(
          "  result = 'abc';  ");
      whenSmoothClean("some arguments");
      thenFinishedWithError();
      thenSysErrContains(
          "Unmatched arguments from index 1: 'some', 'arguments'",
          "",
          "Usage:",
          "smooth clean [-l=<level>]",
          "Try 'smooth help clean' for more information.",
          "");
    }
  }

  @Nested
  class LogLevelOption extends LogLevelOptionTestCase {
    @Override
    protected void whenSmoothCommandWithOption(String option) {
      whenSmooth("clean", option);
    }
  }
}
