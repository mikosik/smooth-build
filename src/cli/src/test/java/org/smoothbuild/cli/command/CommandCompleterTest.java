package org.smoothbuild.cli.command;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.cli.Main.EXIT_CODE_ERROR;
import static org.smoothbuild.common.log.base.Log.fatal;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.task.TaskExecutor;
import org.smoothbuild.common.testing.MemoryReporter;

public class CommandCompleterTest {
  @Test
  void interrupted_exception_is_logged_as_fatal() throws Exception {
    var taskExecutor = mock(TaskExecutor.class);
    doThrow(new InterruptedException()).when(taskExecutor).waitUntilIdle();
    var reporter = new MemoryReporter();
    var commandCompleter = new CommandCompleter(taskExecutor, null, reporter);

    var exitCode = commandCompleter.waitForCompletion();
    assertThat(exitCode).isEqualTo(EXIT_CODE_ERROR);
    assertThat(reporter.logs()).containsExactly(fatal("taskExecutor has been interrupted"));
  }
}
