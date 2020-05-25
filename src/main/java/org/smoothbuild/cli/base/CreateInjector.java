package org.smoothbuild.cli.base;

import static com.google.inject.Stage.PRODUCTION;

import java.io.PrintWriter;

import org.smoothbuild.cli.console.ConsoleModule;
import org.smoothbuild.cli.console.Level;
import org.smoothbuild.cli.taskmatcher.TaskMatcher;
import org.smoothbuild.cli.taskmatcher.TaskMatchers;
import org.smoothbuild.exec.task.TaskModule;
import org.smoothbuild.install.InstallationPathsModule;
import org.smoothbuild.io.fs.FileSystemModule;
import org.smoothbuild.lang.object.db.ObjectDbModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class CreateInjector {
  public static Injector createInjector(PrintWriter out, Level logLevel) {
    return createInjector(out, logLevel, TaskMatchers.ALL);
  }

  public static Injector createInjector(PrintWriter out, Level logLevel, TaskMatcher taskMatcher) {
    return createInjector(new ConsoleModule(out, logLevel, taskMatcher));
  }

  private static Injector createInjector(ConsoleModule consoleModule) {
    return Guice.createInjector(PRODUCTION,
        new TaskModule(),
        new ObjectDbModule(),
        new FileSystemModule(),
        new InstallationPathsModule(),
        consoleModule);
  }
}
