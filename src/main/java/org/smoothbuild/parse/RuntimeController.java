package org.smoothbuild.parse;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.parse.ModuleLoader.loadModule;

import java.util.function.Consumer;

import javax.inject.Inject;

import org.smoothbuild.cli.console.LoggerImpl;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.install.InstallationPaths;
import org.smoothbuild.lang.base.ModulePath;
import org.smoothbuild.lang.runtime.SRuntime;

public class RuntimeController {
  private final SRuntime runtime;
  private final InstallationPaths paths;
  private final Reporter reporter;

  @Inject
  public RuntimeController(SRuntime runtime, InstallationPaths paths, Reporter reporter) {
    this.runtime = runtime;
    this.paths = paths;
    this.reporter = reporter;
  }

  public int setUpRuntimeAndRun(Consumer<SRuntime> runner) {
    reporter.startNewPhase("Parsing");

    for (ModulePath module : paths.allModules()) {
      try (LoggerImpl logger = new LoggerImpl(module.smooth().shorted(), reporter)) {
        loadModule(runtime, module, logger);
      }
      if (reporter.isProblemReported()) {
        reporter.printSummary();
        return EXIT_CODE_ERROR;
      }
    }
    runner.accept(runtime);
    reporter.printSummary();
    return reporter.isProblemReported() ? EXIT_CODE_ERROR : EXIT_CODE_SUCCESS;
  }
}
