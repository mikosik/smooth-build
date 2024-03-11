package org.smoothbuild.app.run;

import static com.google.inject.Stage.PRODUCTION;
import static org.smoothbuild.app.layout.Layout.BIN_DIR_NAME;
import static org.smoothbuild.app.layout.Layout.STANDARD_LIBRARY_DIR_NAME;
import static org.smoothbuild.app.layout.SmoothSpace.BINARY;
import static org.smoothbuild.app.layout.SmoothSpace.PROJECT;
import static org.smoothbuild.app.layout.SmoothSpace.STANDARD_LIBRARY;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.log.Level.INFO;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Path;
import org.smoothbuild.app.layout.BinarySpaceModule;
import org.smoothbuild.app.layout.ProjectSpaceModule;
import org.smoothbuild.app.layout.StandardLibrarySpaceModule;
import org.smoothbuild.app.report.ReportModule;
import org.smoothbuild.app.run.eval.report.ReportMatchers;
import org.smoothbuild.app.wire.AppModule;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.filesystem.base.Space;
import org.smoothbuild.common.filesystem.wiring.DiskFileSystemModule;
import org.smoothbuild.common.log.Level;
import org.smoothbuild.common.log.ReportMatcher;
import org.smoothbuild.virtualmachine.wire.VirtualMachineModule;

public class CreateInjector {
  public static Injector createInjector(Path projectDir, PrintWriter out, Level logLevel) {
    return createInjector(projectDir, out, logLevel, ReportMatchers.ALL);
  }

  public static Injector createInjector(
      Path projectDir, PrintWriter out, Level logLevel, ReportMatcher reportMatcher) {
    Map<Space, Path> spaceToPath = map(
        PROJECT, projectDir,
        STANDARD_LIBRARY, installationDir().resolve(STANDARD_LIBRARY_DIR_NAME),
        BINARY, installationDir().resolve(BIN_DIR_NAME));
    return Guice.createInjector(
        PRODUCTION,
        new AppModule(),
        new VirtualMachineModule(),
        new ProjectSpaceModule(),
        new StandardLibrarySpaceModule(),
        new BinarySpaceModule(),
        new DiskFileSystemModule(spaceToPath),
        new ReportModule(out, reportMatcher, logLevel));
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
        new ReportModule(out, ReportMatchers.ALL, INFO));
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
