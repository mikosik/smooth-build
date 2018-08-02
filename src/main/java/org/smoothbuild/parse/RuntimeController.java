package org.smoothbuild.parse;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.parse.ModuleLoader.loadModule;
import static org.smoothbuild.util.Maybe.value;

import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.smoothbuild.SmoothPaths;
import org.smoothbuild.cli.Console;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.runtime.SRuntime;

public class RuntimeController {
  private final SRuntime runtime;
  private final ValuesDb valuesDb;
  private final SmoothPaths paths;
  private final Console console;

  @Inject
  public RuntimeController(SRuntime runtime, ValuesDb valuesDb, SmoothPaths paths,
      Console console) {
    this.runtime = runtime;
    this.valuesDb = valuesDb;
    this.paths = paths;
    this.console = console;
  }

  public int setUpRuntimeAndRun(Consumer<SRuntime> runner) {
    List<? extends Object> errors = value(null)
        .invoke((v) -> loadModule(runtime, valuesDb, paths.funcsModule()))
        .invoke((v) -> loadModule(runtime, valuesDb, paths.defaultScript()))
        .invokeConsumer(ml -> runner.accept(runtime))
        .errors();
    console.rawErrors(errors);
    console.printFinalSummary();
    return console.isErrorReported() ? EXIT_CODE_ERROR : EXIT_CODE_SUCCESS;
  }
}
