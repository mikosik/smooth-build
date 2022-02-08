package org.smoothbuild.systemtest.cli.command;

import static com.google.common.truth.Truth.assertThat;
import static java.nio.file.Files.createDirectories;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.install.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.install.ProjectPaths.COMPUTATION_CACHE_PATH;
import static org.smoothbuild.install.ProjectPaths.OBJECT_DB_PATH;
import static org.smoothbuild.install.ProjectPaths.PRJ_MOD_PATH;
import static org.smoothbuild.install.ProjectPaths.SMOOTH_LOCK_PATH;
import static org.smoothbuild.install.ProjectPaths.TEMPORARY_PATH;
import static org.smoothbuild.systemtest.CommandWithArgs.cleanCommand;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.io.fs.base.PathS;
import org.smoothbuild.systemtest.CommandWithArgs;
import org.smoothbuild.systemtest.SystemTestCase;
import org.smoothbuild.systemtest.cli.command.common.DefaultModuleTestCase;
import org.smoothbuild.systemtest.cli.command.common.LockFileTestCase;
import org.smoothbuild.systemtest.cli.command.common.LogLevelOptionTestCase;

public class CleanCommandTest {
  @Nested
  class clean_command extends SystemTestCase {
    @Test
    public void deletes_content_of_smooth_dir_except_lock_file() throws IOException {
      createUserModule("""
              result = "abc";
              """);
      createDirInProject(OBJECT_DB_PATH);
      createDirInProject(COMPUTATION_CACHE_PATH);
      createDirInProject(ARTIFACTS_PATH);
      createDirInProject(TEMPORARY_PATH);

      runSmoothClean();
      assertFinishedWithSuccess();
      assertThat(Files.list(smoothDirAbsolutePath()).collect(toList()))
          .containsExactly(absolutePath(SMOOTH_LOCK_PATH.toString()));
    }

    private void createDirInProject(PathS path) throws IOException {
      createDirectories(absolutePath(path.toString()));
    }

    @Test
    public void reports_error_when_user_module_is_missing_and_smooth_dir_exists() throws
        IOException {
      createDirectories(smoothDirAbsolutePath());
      runSmoothClean();
      assertFinishedWithError();
      assertSysOutContains("smooth: error: Directory '" + projectDirOption() + "' doesn't have "
          + PRJ_MOD_PATH.q() + ". Is it really smooth enabled project?");
    }

    @Test
    public void with_args_prints_error() throws Exception {
      createUserModule("""
              result = "abc";
              """);
      runSmoothClean("some", "arguments");
      assertFinishedWithError();
      assertSysErrContains("Unmatched arguments from index");
      assertSysErrContains(
          "Usage:",
          "smooth clean [-d=<projectDir>] [-l=<level>]",
          "Try 'smooth help clean' for more information.",
          "");
    }
  }

  @Nested
  class DefaultModule extends DefaultModuleTestCase {
    @Override
    protected CommandWithArgs commandNameWithArg() {
      return cleanCommand();
    }
  }

  @Nested
  class LockFile extends LockFileTestCase {
    @Override
    protected CommandWithArgs commandNameWithArg() {
      return cleanCommand();
    }
  }

  @Nested
  class LogLevelOption extends LogLevelOptionTestCase {
    @Override
    protected void whenSmoothCommandWithOption(String option) {
      runSmooth(cleanCommand(option));
    }
  }
}
