package org.smoothbuild.slib.cli.command;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.SmoothConstants.ARTIFACTS_PATH;
import static org.smoothbuild.SmoothConstants.HASHED_DB_PATH;
import static org.smoothbuild.SmoothConstants.OUTPUTS_DB_PATH;
import static org.smoothbuild.SmoothConstants.SMOOTH_LOCK_PATH;
import static org.smoothbuild.SmoothConstants.TEMPORARY_PATH;
import static org.smoothbuild.SmoothConstants.USER_MODULE;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.cli.command.CleanCommand;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.slib.AcceptanceTestCase;
import org.smoothbuild.slib.cli.command.common.LockFileTestCase;
import org.smoothbuild.slib.cli.command.common.LogLevelOptionTestCase;

@SuppressWarnings("ClassCanBeStatic")
public class CleanCommandTest {
  @Nested
  class clean_command extends AcceptanceTestCase {
    @Test
    public void deletes_content_of_smooth_dir_except_lock_file() throws IOException {
      givenScript(
          "  result = 'abc';"
      );
      createDirInProject(HASHED_DB_PATH);
      createDirInProject(OUTPUTS_DB_PATH);
      createDirInProject(ARTIFACTS_PATH);
      createDirInProject(TEMPORARY_PATH);

      whenSmoothClean();
      thenFinishedWithSuccess();
      assertThat(smoothDir().list())
          .asList().containsExactly(SMOOTH_LOCK_PATH.lastPart().toString());
    }

    private void createDirInProject(Path projectPath) {
      assertThat(new File(projectDir(), projectPath.toString()).mkdirs()).isTrue();
    }

    @Test
    public void reports_error_when_user_module_is_missing() {
      whenSmoothClean();
      thenFinishedWithError();
      thenSysOutContains("smooth: error: Current dir doesn't have '" + USER_MODULE.smooth().path()
          + "'. Is it really smooth enabled project?");
    }

    @Test
    public void reports_error_when_user_module_is_missing_and_smooth_dir_exists() {
      assertThat(smoothDir().mkdirs())
          .isTrue();
      whenSmoothClean();
      thenFinishedWithError();
      thenSysOutContains("smooth: error: Current dir doesn't have '" + USER_MODULE.smooth().path()
          + "'. Is it really smooth enabled project?");
    }

    @Test
    public void with_arguments_prints_error() throws Exception {
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
  class LockFile extends LockFileTestCase {
    @Override
    protected String[] commandNameWithArgument() {
      return new String[] {CleanCommand.NAME };
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
