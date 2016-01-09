package org.smoothbuild.lang.function;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;

@Singleton
public class Functions {
  private final Map<Name, Function> functions = new HashMap<>();

  public void add(Function function) {
    functions.put(function.name(), function);
  }

  public Function get(Name name) {
    return functions.get(name);
  }

  public boolean contains(Name name) {
    return functions.containsKey(name);
  }

  public Collection<Name> names() {
    return unmodifiableCollection(functions.keySet());
  }
}
