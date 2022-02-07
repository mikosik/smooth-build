package org.smoothbuild.cli.base;

import static com.google.inject.Stage.PRODUCTION;

import java.io.PrintWriter;
import java.nio.file.Path;

import org.smoothbuild.bytecode.ByteCodeModule;
import org.smoothbuild.eval.EvaluateModule;
import org.smoothbuild.install.InstallationModule;
import org.smoothbuild.io.fs.FileSystemModule;
import org.smoothbuild.out.console.ConsoleModule;
import org.smoothbuild.out.log.Level;
import org.smoothbuild.out.log.LoggerModule;
import org.smoothbuild.out.report.TaskMatcher;
import org.smoothbuild.out.report.TaskMatchers;

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
        new EvaluateModule(),
        new ByteCodeModule(),
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
