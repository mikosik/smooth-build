package org.smoothbuild.lang.function;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;

public class Functions {
  private final Map<Name, Function> functions;

  public Functions() {
    this.functions = new HashMap<>();
  }

  public void add(Function function) {
    Name name = function.name();
    if (functions.containsKey(name)) {
      throw new IllegalArgumentException(
          "Function '" + name + "' is already added to this Functions.");
    }
    functions.put(name, function);
  }

  public Function get(Name name) {
    if (!functions.containsKey(name)) {
      throw new IllegalArgumentException("Cannot find function '" + name + "'.\n"
          + "Available functions: " + functions.keySet());
    }
    return functions.get(name);
  }

  public boolean contains(Name name) {
    return functions.containsKey(name);
  }

  public Collection<Name> names() {
    return functions.keySet();
  }

  public Collection<Function> functions() {
    return functions.values();
  }
}
