package org.smoothbuild.parse;

import static org.smoothbuild.util.Maybe.invoke;
import static org.smoothbuild.util.Maybe.invokeWrap;

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
    Maybe<Functions> builtin = moduleLoader.loadModule(new Functions(), paths.funcsModule());
    Maybe<Functions> user = invoke(builtin, b -> moduleLoader.loadModule(b, paths.defaultScript()));
    return invokeWrap(user, builtin, (u, b) -> b.addAll(u));
  }
}
