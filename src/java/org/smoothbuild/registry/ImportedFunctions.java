package org.smoothbuild.registry;

import java.util.Map;
import java.util.Set;

import org.smoothbuild.registry.instantiate.Function;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public class ImportedFunctions {
  private final Map<String, Function> map = Maps.newHashMap();

  public void add(Function function) {
    String name = function.name().simple();
    if (contains(name)) {
      throw new IllegalArgumentException("Function with short name '" + name
          + "' has already been imported from '" + function.name().full() + "'");
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

  public Set<String> names() {
    return ImmutableSet.copyOf(map.keySet());
  }
}
