package org.smoothbuild.run;

import static com.google.inject.Stage.PRODUCTION;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.log.Level.INFO;
import static org.smoothbuild.filesystem.install.InstallationLayout.BIN_DIR_NAME;
import static org.smoothbuild.filesystem.install.InstallationLayout.STANDARD_LIBRARY_DIR_NAME;
import static org.smoothbuild.filesystem.space.SmoothSpace.BINARY;
import static org.smoothbuild.filesystem.space.SmoothSpace.PROJECT;
import static org.smoothbuild.filesystem.space.SmoothSpace.STANDARD_LIBRARY;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Path;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.filesystem.space.DiskFileSystemModule;
import org.smoothbuild.common.filesystem.space.Space;
import org.smoothbuild.common.log.Level;
import org.smoothbuild.filesystem.install.BinarySpaceModule;
import org.smoothbuild.filesystem.install.StandardLibrarySpaceModule;
import org.smoothbuild.filesystem.project.ProjectSpaceModule;
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
    Map<Space, Path> spaceToPath = map(
        PROJECT, projectDir,
        STANDARD_LIBRARY, installationDir().resolve(STANDARD_LIBRARY_DIR_NAME),
        BINARY, installationDir().resolve(BIN_DIR_NAME));
    return Guice.createInjector(
        PRODUCTION,
        new EvaluatorBModule(),
        new EvaluatorSModule(taskMatcher),
        new BytecodeModule(),
        new ProjectSpaceModule(),
        new StandardLibrarySpaceModule(),
        new BinarySpaceModule(),
        new DiskFileSystemModule(spaceToPath),
        new ReportModule(out, logLevel));
  }

  public static Injector createInjector(PrintWriter out) {
    Map<Space, Path> spaceToPath = map(
        STANDARD_LIBRARY, installationDir().resolve(STANDARD_LIBRARY_DIR_NAME),
        BINARY, installationDir().resolve(BIN_DIR_NAME));
    return Guice.createInjector(
        PRODUCTION,
        new StandardLibrarySpaceModule(),
        new BinarySpaceModule(),
        new DiskFileSystemModule(spaceToPath),
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
