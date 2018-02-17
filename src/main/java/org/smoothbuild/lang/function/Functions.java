package org.smoothbuild.lang.function;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.smoothbuild.lang.function.base.Function;

public class Functions {
  private final Map<String, Function> functions;

  public Functions() {
    this.functions = new HashMap<>();
  }

  public void add(Function function) {
    String name = function.name();
    if (functions.containsKey(name)) {
      throw new IllegalArgumentException(
          "Function '" + name + "' is already added to this Functions.");
    }
    functions.put(name, function);
  }

  public Function get(String name) {
    if (!functions.containsKey(name)) {
      throw new IllegalArgumentException("Cannot find function '" + name + "'.\n"
          + "Available functions: " + functions.keySet());
    }
    return functions.get(name);
  }

  public boolean contains(String name) {
    return functions.containsKey(name);
  }

  public Collection<String> names() {
    return functions.keySet();
  }

  public Collection<Function> functions() {
    return functions.values();
  }
}
