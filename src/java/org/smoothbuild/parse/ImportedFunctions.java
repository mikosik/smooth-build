package org.smoothbuild.parse;

import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.smoothbuild.db.task.TaskDb;
import org.smoothbuild.function.base.CachableFunction;
import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Name;
import org.smoothbuild.function.nativ.NativeFunction;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.ProvidedBy;

@Singleton
@ProvidedBy(ImportedFunctionsProvider.class)
public class ImportedFunctions implements SymbolTable {
  private final TaskDb taskDb;
  private final Map<String, Function> map = Maps.newHashMap();

  public ImportedFunctions(TaskDb taskDb) {
    this.taskDb = taskDb;
  }

  public void add(NativeFunction function) {
    Name functionName = function.name();
    String name = functionName.value();
    if (containsFunction(name)) {
      throw new IllegalArgumentException("Function with name " + functionName
          + " has already been imported.");
    } else {
      map.put(name, makeCacheable(function));
    }
  }

  private Function makeCacheable(NativeFunction function) {
    if (function.isCacheable()) {
      return new CachableFunction(taskDb, function);
    } else {
      return function;
    }
  }

  @Override
  public boolean containsFunction(String name) {
    return map.containsKey(name);
  }

  @Override
  public Function getFunction(String name) {
    return map.get(name);
  }

  @Override
  public Set<String> names() {
    return ImmutableSet.copyOf(map.keySet());
  }
}
