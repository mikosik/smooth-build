package org.smoothbuild.lang.function.base;

import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.db.taskresults.TaskResultsDb;
import org.smoothbuild.lang.function.nativ.NativeFunction;

import com.google.common.collect.Maps;

public class ModuleBuilder {
  private final TaskResultsDb taskResultsDb;
  private final Map<Name, Function<?>> functions;

  @Inject
  public ModuleBuilder(TaskResultsDb taskResultsDb) {
    this.taskResultsDb = taskResultsDb;
    this.functions = Maps.newHashMap();
  }

  public void addFunction(NativeFunction<?> function) {
    Name name = function.name();
    if (functions.containsKey(name)) {
      throw new IllegalArgumentException("Function " + name
          + " has been already added to this module.");
    } else {
      functions.put(name, makeCacheable(function));
    }
  }

  private Function<?> makeCacheable(NativeFunction<?> function) {
    if (function.isCacheable()) {
      return new CachableFunction<>(taskResultsDb, function);
    } else {
      return function;
    }
  }

  public Module build() {
    return new ImmutableModule(functions);
  }
}
