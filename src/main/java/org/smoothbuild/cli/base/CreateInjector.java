package org.smoothbuild.cli.base;

import static com.google.inject.Stage.PRODUCTION;

import java.io.PrintWriter;
import java.nio.file.Path;

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
  public static Injector createInjector(Path installationDir, PrintWriter out, Level logLevel) {
    return createInjector(installationDir, out, logLevel, TaskMatchers.ALL);
  }

  public static Injector createInjector(
      Path installationDir, PrintWriter out, Level logLevel, TaskMatcher taskMatcher) {
    ConsoleModule consoleModule = new ConsoleModule(out, logLevel, taskMatcher);
    InstallationPathsModule installationPathsModule = new InstallationPathsModule(installationDir);
    return Guice.createInjector(PRODUCTION,
        new TaskModule(),
        new ObjectDbModule(),
        new FileSystemModule(),
        installationPathsModule,
        consoleModule);
  }
}
