package org.smoothbuild.systemtest.cli.command.common;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.common.base.Strings.unlines;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.CommandWithArgs;
import org.smoothbuild.systemtest.SystemTestContext;
import org.smoothbuild.virtualmachine.testing.func.nativ.Sleep3s;

public abstract class AbstractLockFileTestSuite extends SystemTestContext {
  @Test
  void command_fails_when_lock_file_is_already_acquired() throws IOException, InterruptedException {
    createNativeJar(Sleep3s.class);
    createUserModule(format(
        """
            @Native("%s")
            String sleep3s();
            result = sleep3s();
            """,
        Sleep3s.class.getCanonicalName()));

    SystemTestContext otherTest = new SystemTestContext() {};
    otherTest.init(projectDirAbsolutePath());
    CommandWithArgs commandWithArgs = commandNameWithArg();

    AtomicInteger savedExitCode = new AtomicInteger();
    AtomicReference<String> savedSystemOut = new AtomicReference<>();
    AtomicReference<String> savedSystemErr = new AtomicReference<>();
    Thread thread = new Thread(() -> {
      otherTest.runSmoothBuild("result");
      savedExitCode.set(otherTest.exitCode());
      savedSystemOut.set(otherTest.systemOut());
      savedSystemErr.set(otherTest.systemErr());
    });

    thread.start();
    Thread.sleep(1000);
    runSmooth(commandWithArgs);
    thread.join();

    int otherErrorCode = savedExitCode.get();
    String otherSystemOut = savedSystemOut.get();

    String expectedError = "smooth: error: Another instance of smooth is running for this project.";
    boolean systemOutsMatch =
        systemOut().contains(expectedError) || otherSystemOut.contains(expectedError);
    boolean errorCodesMatch =
        (exitCode() == 0 && otherErrorCode == 2) || (exitCode() == 2 && otherErrorCode == 0);

    if (!(errorCodesMatch && systemOutsMatch)) {
      fail(unlines(
          "this process =================",
          "errorCode = " + exitCode(),
          "systemOut:",
          systemOut(),
          "systemErr:",
          systemErr(),
          "other process =================",
          "errorCode = " + otherErrorCode,
          "systemOut:",
          otherSystemOut,
          "systemErr:",
          savedSystemErr.get()));
    }
  }

  protected abstract CommandWithArgs commandNameWithArg();
}
