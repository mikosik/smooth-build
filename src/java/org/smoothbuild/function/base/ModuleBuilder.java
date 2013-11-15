package org.smoothbuild.function.base;

import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.function.nativ.NativeFunction;
import org.smoothbuild.io.db.task.TaskDb;

import com.google.common.collect.Maps;

public class ModuleBuilder {
  private final TaskDb taskDb;
  private final Map<Name, Function> functions;

  @Inject
  public ModuleBuilder(TaskDb taskDb) {
    this.taskDb = taskDb;
    this.functions = Maps.newHashMap();
  }

  public void addFunction(NativeFunction function) {
    Name name = function.name();
    if (functions.containsKey(name)) {
      throw new IllegalArgumentException("Function " + name
          + " has been already added to this module.");
    } else {
      functions.put(name, makeCacheable(function));
    }
  }

  private Function makeCacheable(NativeFunction function) {
    if (function.isCacheable()) {
      return new CachableFunction(taskDb, function);
    } else {
      return function;
    }
  }

  public Module build() {
    return new ImmutableModule(functions);
  }
}
