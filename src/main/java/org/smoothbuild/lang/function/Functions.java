package org.smoothbuild.lang.function;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;

import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class Functions {
  private final ImmutableMap<Name, Function> functions;

  public Functions() {
    this.functions = ImmutableMap.of();
  }

  private Functions(ImmutableMap<Name, Function> map) {
    this.functions = map;
  }

  public Functions addAll(Functions functions) {
    ImmutableMap<Name, Function> map = builder()
        .putAll(functions.functions)
        .build();
    return new Functions(map);
  }

  public Functions add(Function function) {
    ImmutableMap<Name, Function> map = builder()
        .put(function.name(), function)
        .build();
    return new Functions(map);
  }

  private Builder<Name, Function> builder() {
    return ImmutableMap.<Name, Function> builder()
        .putAll(this.functions);
  }

  public Function get(Name name) {
    if (!functions.containsKey(name)) {
      throw new IllegalArgumentException("Cannot find function " + name + ".\n"
          + "Available functions: " + functions.keySet());
    }
    return functions.get(name);
  }

  public boolean contains(Name name) {
    return functions.containsKey(name);
  }

  public ImmutableMap<Name, Function> nameToFunctionMap() {
    return functions;
  }

  public Collection<Name> names() {
    return unmodifiableCollection(functions.keySet());
  }
}
