package org.smoothbuild.acceptance.cli.command;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.acceptance.CommandWithArgs.cleanCommand;
import static org.smoothbuild.install.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.install.ProjectPaths.HASHED_DB_PATH;
import static org.smoothbuild.install.ProjectPaths.OUTPUTS_DB_PATH;
import static org.smoothbuild.install.ProjectPaths.SMOOTH_LOCK_PATH;
import static org.smoothbuild.install.ProjectPaths.TEMPORARY_PATH;
import static org.smoothbuild.install.ProjectPaths.USER_MODULE_PATH;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.CommandWithArgs;
import org.smoothbuild.acceptance.cli.command.common.DefaultModuleTestCase;
import org.smoothbuild.acceptance.cli.command.common.LockFileTestCase;
import org.smoothbuild.acceptance.cli.command.common.LogLevelOptionTestCase;
import org.smoothbuild.io.fs.base.Path;

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
      assertThat(new File(projectDirAbsolute(), projectPath.toString()).mkdirs()).isTrue();
    }

    @Test
    public void reports_error_when_user_module_is_missing_and_smooth_dir_exists() {
      assertThat(smoothDir().mkdirs())
          .isTrue();
      whenSmoothClean();
      thenFinishedWithError();
      thenSysOutContains("smooth: error: Directory '" + projectDirOption() + "' doesn't have "
          + USER_MODULE_PATH.q() + ". Is it really smooth enabled project?");
    }

    @Test
    public void with_arguments_prints_error() throws Exception {
      givenScript(
          "  result = 'abc';  ");
      whenSmoothClean("some", "arguments");
      thenFinishedWithError();
      thenSysErrContains("Unmatched arguments from index");
      thenSysErrContains(
          "Usage:",
          "smooth clean [-d=<projectDir>] [-l=<level>]",
          "Try 'smooth help clean' for more information.",
          "");
    }
  }

  @Nested
  class DefaultModule extends DefaultModuleTestCase {
    @Override
    protected CommandWithArgs commandNameWithArgument() {
      return cleanCommand();
    }
  }

  @Nested
  class LockFile extends LockFileTestCase {
    @Override
    protected CommandWithArgs commandNameWithArgument() {
      return cleanCommand();
    }
  }

  @Nested
  class LogLevelOption extends LogLevelOptionTestCase {
    @Override
    protected void whenSmoothCommandWithOption(String option) {
      whenSmooth(cleanCommand(option));
    }
  }
}
