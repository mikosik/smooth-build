package org.smoothbuild.cli.base;

import static com.google.inject.Stage.PRODUCTION;

import java.io.PrintWriter;
import java.nio.file.Path;

import org.smoothbuild.cli.console.ConsoleModule;
import org.smoothbuild.cli.console.Level;
import org.smoothbuild.cli.console.LoggerModule;
import org.smoothbuild.cli.taskmatcher.TaskMatcher;
import org.smoothbuild.cli.taskmatcher.TaskMatchers;
import org.smoothbuild.db.bytecode.ByteDbModule;
import org.smoothbuild.exec.ExecuteModule;
import org.smoothbuild.install.InstallationModule;
import org.smoothbuild.io.fs.FileSystemModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class CreateInjector {
  public static Injector createInjector(Path projectDir, Path installationDir, PrintWriter out,
      Level logLevel) {
    return createInjector(projectDir, installationDir, out, logLevel, TaskMatchers.ALL);
  }

  public static Injector createInjector(Path projectDir, Path installationDir, PrintWriter out,
      Level logLevel, TaskMatcher taskMatcher) {
    return Guice.createInjector(PRODUCTION,
        new ExecuteModule(),
        new ByteDbModule(),
        new FileSystemModule(projectDir),
        new InstallationModule(installationDir),
        new LoggerModule(logLevel, taskMatcher),
        new ConsoleModule(out));
  }

  public static Injector createInjector(Path installationDir, PrintWriter out) {
    return Guice.createInjector(PRODUCTION,
        new FileSystemModule(),
        new InstallationModule(installationDir),
        new ConsoleModule(out));
  }
}
