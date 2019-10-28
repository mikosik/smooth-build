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
import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.lang.runtime.SRuntime;

public class RuntimeController {
  private final SRuntime runtime;
  private final ObjectsDb objectsDb;
  private final SmoothPaths paths;
  private final Console console;

  @Inject
  public RuntimeController(SRuntime runtime, ObjectsDb objectsDb, SmoothPaths paths,
      Console console) {
    this.runtime = runtime;
    this.objectsDb = objectsDb;
    this.paths = paths;
    this.console = console;
  }

  public int setUpRuntimeAndRun(Consumer<SRuntime> runner) {
    List<?> errors = value(null)
        .invoke((v) -> loadModule(runtime, objectsDb, paths.funcsModule()))
        .invoke((v) -> loadModule(runtime, objectsDb, paths.defaultScript()))
        .invokeConsumer(ml -> runner.accept(runtime))
        .errors();
    console.errors(errors);
    console.printFinalSummary();
    return console.isProblemReported() ? EXIT_CODE_ERROR : EXIT_CODE_SUCCESS;
  }
}
