package org.smoothbuild.systemtest.cli.command;

import static com.google.common.truth.Truth.assertThat;
import static java.nio.file.Files.createDirectories;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.filesystem.project.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.filesystem.project.ProjectPaths.COMPUTATION_CACHE_PATH;
import static org.smoothbuild.filesystem.project.ProjectPaths.DEFAULT_MODULE_PATH;
import static org.smoothbuild.filesystem.project.ProjectPaths.HASHED_DB_PATH;
import static org.smoothbuild.filesystem.project.ProjectPaths.SMOOTH_LOCK_PATH;
import static org.smoothbuild.systemtest.CommandWithArgs.cleanCommand;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.systemtest.CommandWithArgs;
import org.smoothbuild.systemtest.SystemTestCase;
import org.smoothbuild.systemtest.cli.command.common.AbstractDefaultModuleTestSuite;
import org.smoothbuild.systemtest.cli.command.common.AbstractLockFileTestSuite;
import org.smoothbuild.systemtest.cli.command.common.AbstractLogLevelOptionTestSuite;

public class CleanCommandTest {
  @Nested
  class clean_command extends SystemTestCase {
    @Test
    public void deletes_content_of_smooth_dir_except_lock_file() throws IOException {
      createUserModule("""
              result = "abc";
              """);
      createDirInProject(HASHED_DB_PATH);
      createDirInProject(COMPUTATION_CACHE_PATH);
      createDirInProject(ARTIFACTS_PATH);

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
      assertSysOutContains("smooth: error: Current directory doesn't have "
          + DEFAULT_MODULE_PATH.q() + ". Is it really smooth enabled project?");
    }

    @Test
    public void with_args_prints_error() throws Exception {
      createUserModule("""
              result = "abc";
              """);
      runSmoothClean("some", "arguments");
      assertFinishedWithError();
      assertSysErrContains("Unmatched arguments from index");
      assertSysErrContains("""
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
