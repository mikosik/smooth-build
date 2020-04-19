package org.smoothbuild.parse;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.parse.ModuleLoader.loadModule;

import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.smoothbuild.ModulePath;
import org.smoothbuild.SmoothPaths;
import org.smoothbuild.cli.console.LoggerImpl;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.lang.runtime.SRuntime;

public class RuntimeController {
  private final SRuntime runtime;
  private final SmoothPaths paths;
  private final Reporter reporter;

  @Inject
  public RuntimeController(SRuntime runtime, SmoothPaths paths, Reporter reporter) {
    this.runtime = runtime;
    this.paths = paths;
    this.reporter = reporter;
  }

  public int setUpRuntimeAndRun(Consumer<SRuntime> runner) {
    reporter.newSection("Parsing");

    for (ModulePath module : List.of(paths.funcsModule(), paths.userModule())) {
      try (LoggerImpl logger = new LoggerImpl(module.shortPath(), reporter)) {
        loadModule(runtime, module, logger);
      }
      if (reporter.isProblemReported()) {
        reporter.printFinalSummary();
        return EXIT_CODE_ERROR;
      }
    }
    runner.accept(runtime);
    reporter.printFinalSummary();
    return reporter.isProblemReported() ? EXIT_CODE_ERROR : EXIT_CODE_SUCCESS;
  }
}
