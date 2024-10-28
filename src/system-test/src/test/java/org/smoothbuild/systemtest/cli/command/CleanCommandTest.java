package org.smoothbuild.systemtest.cli.command;

import static com.google.common.truth.Truth.assertThat;
import static java.nio.file.Files.createDirectories;
import static org.smoothbuild.cli.layout.Layout.ARTIFACTS_PATH;
import static org.smoothbuild.cli.layout.Layout.BYTECODE_DB_PATH;
import static org.smoothbuild.cli.layout.Layout.COMPUTATION_CACHE_PATH;
import static org.smoothbuild.cli.layout.Layout.DEFAULT_MODULE_PATH;
import static org.smoothbuild.cli.layout.Layout.SMOOTH_LOCK_PATH;
import static org.smoothbuild.systemtest.CommandWithArgs.cleanCommand;

import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.systemtest.CommandWithArgs;
import org.smoothbuild.systemtest.SystemTestCase;
import org.smoothbuild.systemtest.cli.command.common.AbstractDefaultModuleTestSuite;
import org.smoothbuild.systemtest.cli.command.common.AbstractLockFileTestSuite;
import org.smoothbuild.systemtest.cli.command.common.AbstractLogLevelOptionTestSuite;

public class CleanCommandTest {
  @Nested
  class clean_command extends SystemTestCase {
    @Test
    void deletes_content_of_smooth_dir_except_lock_file() throws IOException {
      createUserModule("""
              result = "abc";
              """);
      createDirInProject(BYTECODE_DB_PATH);
      createDirInProject(COMPUTATION_CACHE_PATH);
      createDirInProject(ARTIFACTS_PATH);

      runSmoothClean();
      assertFinishedWithSuccess();
      assertThat(Files.list(smoothDirAbsolutePath()).toList())
          .containsExactly(absolutePath(SMOOTH_LOCK_PATH.toString()));
    }

    private void createDirInProject(Path path) throws IOException {
      createDirectories(absolutePath(path.toString()));
    }

    @Test
    void reports_error_when_user_module_is_missing_and_smooth_dir_exists() throws IOException {
      createDirectories(smoothDirAbsolutePath());
      runSmoothClean();
      assertFinishedWithError();
      assertSystemOutContains("smooth: error: Current directory doesn't have "
          + DEFAULT_MODULE_PATH.q() + ". Is it really smooth enabled project?");
    }

    @Test
    void with_args_prints_error() throws Exception {
      createUserModule("""
              result = "abc";
              """);
      runSmoothClean("some", "arguments");
      assertFinishedWithError();
      assertSystemErrContains("Unmatched arguments from index");
      assertSystemErrContains(
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
    protected void whenSmoothCommandWithOption(String option) {
      runSmooth(cleanCommand(option));
    }
  }
}
