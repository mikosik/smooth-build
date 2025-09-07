package org.smoothbuild.systemtest.cli.command;

import static com.google.common.truth.Truth.assertThat;
import static java.nio.file.Files.createDirectories;
import static org.smoothbuild.cli.layout.Layout.DEFAULT_MODULE_PATH;
import static org.smoothbuild.cli.layout.Layout.SMOOTH_LOCK_PATH;
import static org.smoothbuild.systemtest.CommandWithArgs.cleanCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.CommandWithArgs;
import org.smoothbuild.systemtest.SystemTestContext;
import org.smoothbuild.systemtest.SystemTestOutput;
import org.smoothbuild.systemtest.cli.command.common.AbstractDefaultModuleTestSuite;
import org.smoothbuild.systemtest.cli.command.common.AbstractLockFileTestSuite;
import org.smoothbuild.systemtest.cli.command.common.AbstractLogLevelOptionTestSuite;

public class CleanCommandTest {
  @Nested
  class clean_command extends SystemTestContext {
    @Test
    void deletes_content_of_smooth_dir_except_lock_file() throws IOException {
      createUserModule("""
              result = "abc";
              """);
      createDirInProject(BYTECODE_DB_PATH);
      createDirInProject(COMPUTATION_DB_PATH);
      createDirInProject(ARTIFACTS_PATH);

      var output = runSmoothClean();
      output.assertFinishedWithSuccess();
      try (var list = Files.list(smoothDirAbsolutePath())) {
        assertThat(list.toList()).containsExactly(absolutePath(SMOOTH_LOCK_PATH.toString()));
      }
    }

    private void createDirInProject(Path path) throws IOException {
      createDirectories(absolutePath(path.toString()));
    }

    @Test
    void reports_error_when_user_module_is_missing_and_smooth_dir_exists() throws IOException {
      createDirectories(smoothDirAbsolutePath());
      var output = runSmoothClean();
      output.assertFinishedWithError();
      output.assertSystemOutContains("smooth: error: Current directory doesn't have "
          + DEFAULT_MODULE_PATH.q() + ". Is it really smooth enabled project?");
    }

    @Test
    void with_args_prints_error() throws Exception {
      createUserModule("""
              result = "abc";
              """);
      var output = runSmoothClean("some", "arguments");
      output.assertFinishedWithError();
      output.assertSystemErrContains("Unmatched arguments from index");
      output.assertSystemErrContains(
          """
          Usage:
          smooth clean [-l=<level>]
          Try 'smooth help clean' for more information.
          """);
    }
  }

  @Nested
  class DefaultModule extends AbstractDefaultModuleTestSuite {
    @Override
    protected CommandWithArgs commandNameWithArg() {
      return cleanCommand();
    }
  }

  @Nested
  class LockFile extends AbstractLockFileTestSuite {
    @Override
    protected CommandWithArgs commandNameWithArg() {
      return cleanCommand();
    }
  }

  @Nested
  class LogLevelOption extends AbstractLogLevelOptionTestSuite {
    @Override
    protected SystemTestOutput whenSmoothCommandWithOption(String option) {
      return runSmooth(cleanCommand(option));
    }
  }
}
