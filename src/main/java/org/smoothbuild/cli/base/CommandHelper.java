package org.smoothbuild.cli.base;

import static com.google.inject.Guice.createInjector;
import static com.google.inject.Stage.PRODUCTION;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.SMOOTH_LOCK_PATH;
import static org.smoothbuild.SmoothConstants.USER_MODULE;
import static org.smoothbuild.util.LockFile.lockFile;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.util.function.Function;

import org.smoothbuild.MainModule;
import org.smoothbuild.cli.console.Console;

import com.google.inject.Injector;

public class CommandHelper {
  public static int runCommandExclusively(Function<Injector, Integer> invoker) {
    return runCommandExclusively(new ReportModule(), invoker);
  }

  public static int runCommandExclusively(
      ReportModule reportModule, Function<Injector, Integer> invoker) {
    Console console = new Console();
    if (!Files.exists(USER_MODULE.smooth().path())) {
      console.error("Current dir doesn't have '" + USER_MODULE.smooth().path()
          + "'. Is it really smooth enabled project?");
      return EXIT_CODE_ERROR;
    }
    FileLock fileLock = lockFile(SMOOTH_LOCK_PATH.toJPath());
    if (fileLock == null) {
      return EXIT_CODE_ERROR;
    }
    Channel channel = fileLock.acquiredBy();
    try (channel) {
      return runCommand(reportModule, invoker);
    } catch (IOException e) {
      console.error("Error closing file lock.");
      return EXIT_CODE_ERROR;
    }
  }

  public static Integer runCommand(Function<Injector, Integer> invoker) {
    return runCommand(new ReportModule(), invoker);
  }

  public static Integer runCommand(ReportModule reportModule, Function<Injector, Integer> invoker) {
    Injector injector = createInjector(PRODUCTION, new MainModule(), reportModule);
    return invoker.apply(injector);
  }
}
