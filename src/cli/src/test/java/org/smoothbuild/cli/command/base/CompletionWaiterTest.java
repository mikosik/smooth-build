package org.smoothbuild.cli.command.base;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.cli.Main.EXIT_CODE_ERROR;
import static org.smoothbuild.common.log.base.Log.fatal;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.dagger.CommonTestContext;
import org.smoothbuild.common.testing.TestReporter;

public class CompletionWaiterTest extends CommonTestContext {
  @Test
  void interrupted_exception_is_logged_as_fatal() throws Exception {
    var reporter = new TestReporter();
    var statusPrinter = mock(StatusPrinter.class);
    var completionWaiter = new CompletionWaiter(statusPrinter, reporter);

    @SuppressWarnings("unchecked")
    Promise<Maybe<Integer>> promise = mock(Promise.class);
    doThrow(InterruptedException.class).when(promise).getBlocking();
    var exitCode = completionWaiter.waitForCompletion(promise);
    assertThat(exitCode).isEqualTo(EXIT_CODE_ERROR);
    assertThat(reporter.logs()).containsExactly(fatal("main thread has been interrupted"));
  }
}
