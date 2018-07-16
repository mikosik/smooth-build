package org.smoothbuild.parse;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.SmoothPaths;
import org.smoothbuild.lang.runtime.SRuntime;

public class RuntimeLoader {
  private final SRuntime runtime;
  private final ModuleLoader moduleLoader;
  private final SmoothPaths paths;

  @Inject
  public RuntimeLoader(SRuntime runtime, ModuleLoader moduleLoader, SmoothPaths paths) {
    this.runtime = runtime;
    this.moduleLoader = moduleLoader;
    this.paths = paths;
  }

  public List<? extends Object> load() {
    List<? extends Object> errors = moduleLoader.loadModule(runtime, paths.funcsModule());
    if (!errors.isEmpty()) {
      return errors;
    }
    errors = moduleLoader.loadModule(runtime, paths.defaultScript());
    if (!errors.isEmpty()) {
      return errors;
    }
    return new ArrayList<>();
  }
}
