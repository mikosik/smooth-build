package org.smoothbuild.run;

import static com.google.inject.Stage.PRODUCTION;
import static org.smoothbuild.filesystem.install.InstallationLayout.BIN_DIR_NAME;
import static org.smoothbuild.filesystem.install.InstallationLayout.STD_LIB_DIR_NAME;
import static org.smoothbuild.filesystem.space.Space.BINARY;
import static org.smoothbuild.filesystem.space.Space.PROJECT;
import static org.smoothbuild.filesystem.space.Space.STANDARD_LIBRARY;
import static org.smoothbuild.out.log.Level.INFO;

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

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class CreateInjector {
  public static Injector createInjector(Path projectDir, PrintWriter out,
      Level logLevel) {
    return createInjector(projectDir, out, logLevel, TaskMatchers.ALL);
  }

  public static Injector createInjector(Path projectDir, PrintWriter out,
      Level logLevel, TaskMatcher taskMatcher) {
    var installationDir = installationDir();
    var spaceToPath = ImmutableMap.of(
        PROJECT, projectDir,
        STANDARD_LIBRARY, installationDir.resolve(STD_LIB_DIR_NAME),
        BINARY, installationDir.resolve(BIN_DIR_NAME));
    return Guice.createInjector(PRODUCTION,
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
    var installationDir = installationDir();
    var spaceToPath = ImmutableMap.of(
        STANDARD_LIBRARY, installationDir.resolve(STD_LIB_DIR_NAME),
        BINARY, installationDir.resolve(BIN_DIR_NAME));
    return Guice.createInjector(PRODUCTION,
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
      var uri = CreateInjector.class.getProtectionDomain()
          .getCodeSource()
          .getLocation()
          .toURI();
      return Path.of(uri).getParent();
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
}
