package org.smoothbuild.systemtest.cli.command.common;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.common.Strings.unlines;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.CommandWithArgs;
import org.smoothbuild.systemtest.SystemTestCase;
import org.smoothbuild.testing.func.nativ.Sleep3s;

public abstract class AbstractLockFileTestSuite extends SystemTestCase {
  @Test
  public void command_fails_when_lock_file_is_already_acquired() throws IOException,
      InterruptedException {
    createNativeJar(Sleep3s.class);
    createUserModule(format("""
            @Native("%s")
            String sleep3s();
            result = sleep3s();
            """, Sleep3s.class.getCanonicalName()));

    SystemTestCase otherTest = new SystemTestCase() {};
    otherTest.init(projectDirAbsolutePath());
    CommandWithArgs commandWithArgs = commandNameWithArg();

    AtomicInteger savedExitCode = new AtomicInteger();
    AtomicReference<String> savedSysOut = new AtomicReference<>();
    AtomicReference<String> savedSysErr = new AtomicReference<>();
    Thread thread = new Thread(() -> {
      otherTest.runSmoothBuild("result");
      savedExitCode.set(otherTest.exitCode());
      savedSysOut.set(otherTest.sysOut());
      savedSysErr.set(otherTest.sysErr());
    });

    thread.start();
    runSmooth(commandWithArgs);
    thread.join();

    int otherErrorCode = savedExitCode.get();
    String otherSysOut = savedSysOut.get();

    String expectedError = "smooth: error: Another instance of smooth is running for this project.";
    boolean sysOutsMatch =
        sysOut().contains(expectedError) || otherSysOut.contains(expectedError);
    boolean errorCodesMatch =
        (exitCode() == 0 && otherErrorCode == 2) || (exitCode() == 2 && otherErrorCode == 0);

    if (!(errorCodesMatch && sysOutsMatch)) {
      fail(unlines(
          "this process =================",
          "errorCode = " + exitCode(),
          "sysOut:",
          sysOut(),
          "sysErr:",
          sysErr(),
          "other process =================",
          "errorCode = " + otherErrorCode,
          "sysOut:",
          otherSysOut,
          "sysErr:",
          savedSysErr.get()
      ));
    }
  }

  protected abstract CommandWithArgs commandNameWithArg();
}
