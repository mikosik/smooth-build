package org.smoothbuild.parse;

import static org.smoothbuild.util.Maybe.errors;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.SmoothPaths;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.util.Maybe;

public class RuntimeLoader {
  private final ModuleLoader moduleLoader;
  private final SmoothPaths paths;

  @Inject
  public RuntimeLoader(ModuleLoader moduleLoader, SmoothPaths paths) {
    this.moduleLoader = moduleLoader;
    this.paths = paths;
  }

  public Maybe<Functions> loadFunctions() {
    Functions functions = new Functions();
    List<? extends Object> errors = moduleLoader.loadModule(functions, paths.funcsModule());
    if (!errors.isEmpty()) {
      return errors(errors);
    }
    errors = moduleLoader.loadModule(functions, paths.defaultScript());
    if (!errors.isEmpty()) {
      return errors(errors);
    }
    return Maybe.value(functions);
  }
}
