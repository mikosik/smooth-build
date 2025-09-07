package org.smoothbuild.systemtest.cli.command.common;

import static java.lang.String.format;
import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.common.base.Strings.unlines;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.CommandWithArgs;
import org.smoothbuild.systemtest.SystemTestContext;
import org.smoothbuild.systemtest.SystemTestOutput;
import org.smoothbuild.virtualmachine.testing.func.nativ.Sleep3s;

public abstract class AbstractLockFileTestSuite extends SystemTestContext {
  @Test
  void command_fails_when_lock_file_is_already_acquired() throws Exception {
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

    SystemTestOutput thisOutput;
    SystemTestOutput thatOutput;
    try (var executorService = newVirtualThreadPerTaskExecutor()) {
      var future = executorService.submit(() -> otherTest.runSmoothBuild("result"));
      Thread.sleep(1000);
      thisOutput = runSmooth(commandWithArgs);
      thatOutput = future.get();
    }

    String expectedError = "smooth: error: Another instance of smooth is running for this project.";
    boolean systemOutsMatch = thisOutput.systemOut().contains(expectedError)
        || thatOutput.systemOut().contains(expectedError);
    boolean errorCodesMatch = (thisOutput.exitCode() == 0 && thatOutput.exitCode() == 2)
        || (thisOutput.exitCode() == 2 && thatOutput.exitCode() == 0);

    if (!(errorCodesMatch && systemOutsMatch)) {
      fail(unlines(
          "this process =================",
          "errorCode = " + thisOutput.exitCode(),
          "systemOut:",
          thisOutput.systemOut(),
          "systemErr:",
          thisOutput.systemErr(),
          "other process =================",
          "errorCode = " + thatOutput.exitCode(),
          "systemOut:",
          thatOutput.systemOut(),
          "systemErr:",
          thatOutput.systemErr()));
    }
  }

  protected abstract CommandWithArgs commandNameWithArg();
}
