package org.smoothbuild.parse;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.parse.ModuleLoader.loadModule;

import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.smoothbuild.ModulePath;
import org.smoothbuild.SmoothPaths;
import org.smoothbuild.cli.console.Console;
import org.smoothbuild.cli.console.LoggerImpl;
import org.smoothbuild.lang.runtime.SRuntime;

public class RuntimeController {
  private final SRuntime runtime;
  private final SmoothPaths paths;
  private final Console console;

  @Inject
  public RuntimeController(SRuntime runtime, SmoothPaths paths, Console console) {
    this.runtime = runtime;
    this.paths = paths;
    this.console = console;
  }

  public int setUpRuntimeAndRun(Consumer<SRuntime> runner) {
    console.println("Parsing");

    for (ModulePath module : List.of(paths.funcsModule(), paths.userModule())) {
      try (LoggerImpl logger = new LoggerImpl(module.shortPath(), console)) {
        loadModule(runtime, module, logger);
      }
      if (console.isProblemReported()) {
        console.printFinalSummary();
        return EXIT_CODE_ERROR;
      }
    }
    runner.accept(runtime);
    console.printFinalSummary();
    return console.isProblemReported() ? EXIT_CODE_ERROR : EXIT_CODE_SUCCESS;
  }
}
