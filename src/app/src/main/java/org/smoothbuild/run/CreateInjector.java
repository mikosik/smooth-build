package org.smoothbuild.run;

import static com.google.inject.Stage.PRODUCTION;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.log.Level.INFO;
import static org.smoothbuild.layout.Layout.BIN_DIR_NAME;
import static org.smoothbuild.layout.Layout.STANDARD_LIBRARY_DIR_NAME;
import static org.smoothbuild.layout.SmoothSpace.BINARY;
import static org.smoothbuild.layout.SmoothSpace.PROJECT;
import static org.smoothbuild.layout.SmoothSpace.STANDARD_LIBRARY;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Path;
import org.smoothbuild.SandboxHashModule;
import org.smoothbuild.VirtualMachineConfigurationModule;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.filesystem.base.Space;
import org.smoothbuild.common.filesystem.space.DiskFileSystemModule;
import org.smoothbuild.common.log.Level;
import org.smoothbuild.layout.BinarySpaceModule;
import org.smoothbuild.layout.ProjectSpaceModule;
import org.smoothbuild.layout.StandardLibrarySpaceModule;
import org.smoothbuild.out.report.ReportModule;
import org.smoothbuild.run.eval.EvaluatorSModule;
import org.smoothbuild.run.eval.report.TaskMatcher;
import org.smoothbuild.run.eval.report.TaskMatchers;
import org.smoothbuild.virtualmachine.VirtualMachineModule;

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
        new VirtualMachineConfigurationModule(),
        new SandboxHashModule(),
        new EvaluatorSModule(taskMatcher),
        new VirtualMachineModule(),
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
