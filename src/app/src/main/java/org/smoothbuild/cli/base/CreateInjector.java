package org.smoothbuild.cli.base;

import static com.google.inject.Stage.PRODUCTION;
import static org.smoothbuild.out.log.Level.INFO;

import java.io.PrintWriter;
import java.nio.file.Path;

import org.smoothbuild.install.InstallationFileSystemModule;
import org.smoothbuild.install.ProjectFileSystemModule;
import org.smoothbuild.out.log.Level;
import org.smoothbuild.out.report.ReportModule;
import org.smoothbuild.run.eval.EvaluatorSModule;
import org.smoothbuild.run.eval.report.TaskMatcher;
import org.smoothbuild.run.eval.report.TaskMatchers;
import org.smoothbuild.vm.bytecode.BytecodeModule;
import org.smoothbuild.vm.evaluate.EvaluatorBModule;

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
        new EvaluatorBModule(),
        new EvaluatorSModule(taskMatcher),
        new BytecodeModule(),
        new ProjectFileSystemModule(projectDir),
        new InstallationFileSystemModule(installationDir),
        new ReportModule(out, logLevel));
  }

  public static Injector createInjector(Path installationDir, PrintWriter out) {
    return Guice.createInjector(PRODUCTION,
        new InstallationFileSystemModule(installationDir),
        new ReportModule(out, INFO));
  }
}
