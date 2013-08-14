package org.smoothbuild.registry;

import java.util.Map;

import org.smoothbuild.registry.exc.FunctionAlreadyRegisteredException;
import org.smoothbuild.registry.instantiate.Function;

import com.google.common.collect.Maps;

public class ImportedFunctions {
  private final Map<String, Function> map = Maps.newHashMap();

  public void add(Function function) throws FunctionAlreadyRegisteredException {
    String name = function.name().name();
    if (contains(name)) {
      // TODO add more details to the exception: function definition class,
      // builtin/plugin, plugin jar file
      throw new FunctionAlreadyRegisteredException(name);
    } else {
      map.put(name, function);
    }
  }

  public boolean contains(String name) {
    return map.containsKey(name);
  }

  public Function get(String name) {
    Function function = map.get(name);
    if (function == null) {
      throw new IllegalArgumentException("Function '" + name + "' doesn't exist.");
    }
    return function;
  }
}
