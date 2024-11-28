package org.smoothbuild.cli.command.base;

import static com.google.inject.Stage.PRODUCTION;
import static org.smoothbuild.cli.layout.Aliases.INSTALL_ALIAS;
import static org.smoothbuild.cli.layout.Aliases.LIBRARY_ALIAS;
import static org.smoothbuild.cli.layout.Aliases.PROJECT_ALIAS;
import static org.smoothbuild.cli.layout.Layout.BIN_DIR_NAME;
import static org.smoothbuild.cli.layout.Layout.STANDARD_LIBRARY_DIR_NAME;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.log.base.Level.INFO;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Path;
import org.smoothbuild.cli.CliWiring;
import org.smoothbuild.cli.match.ReportMatchers;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.filesystem.FileSystemWiring;
import org.smoothbuild.common.filesystem.base.Alias;
import org.smoothbuild.common.log.base.Level;
import org.smoothbuild.common.log.report.ReportMatcher;
import org.smoothbuild.common.log.report.ReportWiring;
import org.smoothbuild.compilerbackend.CompilerBackendWiring;
import org.smoothbuild.evaluator.EvaluatorWiring;
import org.smoothbuild.virtualmachine.wire.VmWiring;

public class CreateInjector {
  public static Injector createInjector(Path projectDir, PrintWriter out, Level logLevel) {
    return createInjector(projectDir, out, logLevel, ReportMatchers.ALL, ReportMatchers.ALL);
  }

  public static Injector createInjector(
      Path projectDir,
      PrintWriter out,
      Level logLevel,
      ReportMatcher filterTasks,
      ReportMatcher filterTraces) {
    var installationDir = installationDir();
    Map<Alias, Path> aliasToPath = map(
        PROJECT_ALIAS, projectDir,
        LIBRARY_ALIAS, installationDir.resolve(STANDARD_LIBRARY_DIR_NAME),
        INSTALL_ALIAS, installationDir.resolve(BIN_DIR_NAME));
    return Guice.createInjector(
        PRODUCTION,
        new CliWiring(),
        new EvaluatorWiring(),
        new CompilerBackendWiring(),
        new VmWiring(),
        new FileSystemWiring(aliasToPath),
        new ReportWiring(out, logLevel, filterTasks, filterTraces));
  }

  public static Injector createInjector(PrintWriter out) {
    var installationDir = installationDir();
    Map<Alias, Path> aliasToPath = map(
        LIBRARY_ALIAS, installationDir.resolve(STANDARD_LIBRARY_DIR_NAME),
        INSTALL_ALIAS, installationDir.resolve(BIN_DIR_NAME));
    return Guice.createInjector(
        PRODUCTION,
        new CliWiring(),
        new FileSystemWiring(aliasToPath),
        new ReportWiring(out, INFO, ReportMatchers.ALL, ReportMatchers.ALL));
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
