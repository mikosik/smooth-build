package org.smoothbuild.run;

import static com.google.inject.Stage.PRODUCTION;
import static org.smoothbuild.out.log.Level.INFO;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Path;
import org.smoothbuild.filesystem.install.BinarySpaceModule;
import org.smoothbuild.filesystem.install.StandardLibrarySpaceModule;
import org.smoothbuild.filesystem.project.ProjectSpaceModule;
import org.smoothbuild.filesystem.space.DiskFileSystemModule;
import org.smoothbuild.out.log.Level;
import org.smoothbuild.out.report.ReportModule;
import org.smoothbuild.run.eval.EvaluatorSModule;
import org.smoothbuild.run.eval.report.TaskMatcher;
import org.smoothbuild.run.eval.report.TaskMatchers;
import org.smoothbuild.vm.bytecode.BytecodeModule;
import org.smoothbuild.vm.evaluate.EvaluatorBModule;

public class CreateInjector {
  public static Injector createInjector(Path projectDir, PrintWriter out, Level logLevel) {
    return createInjector(projectDir, out, logLevel, TaskMatchers.ALL);
  }

  public static Injector createInjector(
      Path projectDir, PrintWriter out, Level logLevel, TaskMatcher taskMatcher) {
    return Guice.createInjector(
        PRODUCTION,
        new EvaluatorBModule(),
        new EvaluatorSModule(taskMatcher),
        new BytecodeModule(),
        new ProjectSpaceModule(),
        new StandardLibrarySpaceModule(),
        new BinarySpaceModule(),
        new DiskFileSystemModule(installationDir(), projectDir),
        new ReportModule(out, logLevel));
  }

  public static Injector createInjector(PrintWriter out) {
    return Guice.createInjector(
        PRODUCTION,
        new StandardLibrarySpaceModule(),
        new BinarySpaceModule(),
        new DiskFileSystemModule(installationDir()),
        new ReportModule(out, INFO));
  }

  private static Path installationDir() {
    return smoothJarPath().getParent();
  }

  private static Path smoothJarPath() {
    try {
      var uri = CreateInjector.class
          .getProtectionDomain()
          .getCodeSource()
          .getLocation()
          .toURI();
      return Path.of(uri).getParent();
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
}
