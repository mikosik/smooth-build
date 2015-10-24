package org.smoothbuild.task.exec;

import java.util.Set;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.module.Module;

public class ExecutionData {
  private final Set<Name> functions;
  private final Module module;

  public ExecutionData(Set<Name> functions, Module module) {
    this.functions = functions;
    this.module = module;
  }

  public Set<Name> functions() {
    return functions;
  }

  public Module module() {
    return module;
  }
}
