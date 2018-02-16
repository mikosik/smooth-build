package org.smoothbuild.parse;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.SmoothPaths;

public class RuntimeLoader {
  private final ModuleLoader moduleLoader;
  private final SmoothPaths paths;

  @Inject
  public RuntimeLoader(ModuleLoader moduleLoader, SmoothPaths paths) {
    this.moduleLoader = moduleLoader;
    this.paths = paths;
  }

  public List<? extends Object> load() {
    List<? extends Object> errors = moduleLoader.loadModule(paths.funcsModule());
    if (!errors.isEmpty()) {
      return errors;
    }
    errors = moduleLoader.loadModule(paths.defaultScript());
    if (!errors.isEmpty()) {
      return errors;
    }
    return new ArrayList<>();
  }
}
