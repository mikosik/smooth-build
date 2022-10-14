package org.smoothbuild.cli.base;

import static com.google.inject.Stage.PRODUCTION;
import static org.smoothbuild.out.log.Level.INFO;

import java.io.PrintWriter;
import java.nio.file.Path;

import org.smoothbuild.bytecode.BytecodeModule;
import org.smoothbuild.fs.FileSystemModule;
import org.smoothbuild.install.InstallationModule;
import org.smoothbuild.out.log.Level;
import org.smoothbuild.out.report.ReportModule;
import org.smoothbuild.vm.VmModule;
import org.smoothbuild.vm.report.TaskMatcher;
import org.smoothbuild.vm.report.TaskMatchers;

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
        new VmModule(taskMatcher),
        new BytecodeModule(),
        new FileSystemModule(projectDir),
        new InstallationModule(installationDir),
        new ReportModule(out, logLevel));
  }

  public static Injector createInjector(Path installationDir, PrintWriter out) {
    return Guice.createInjector(PRODUCTION,
        new FileSystemModule(),
        new InstallationModule(installationDir),
        new ReportModule(out, INFO));
  }
}
