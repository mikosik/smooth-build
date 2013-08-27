package org.smoothbuild.parse;

import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.smoothbuild.function.base.Function;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.ProvidedBy;

@Singleton
@ProvidedBy(ImportedFunctionsProvider.class)
public class ImportedFunctions implements SymbolTable {
  private final Map<String, Function> map = Maps.newHashMap();

  public void add(Function function) {
    String name = function.name().simple();
    if (containsFunction(name)) {
      throw new IllegalArgumentException("Function with short name '" + name
          + "' has already been imported from '" + function.name().full() + "'");
    } else {
      map.put(name, function);
    }
  }

  @Override
  public boolean containsFunction(String name) {
    return map.containsKey(name);
  }

  @Override
  public Function getFunction(String name) {
    Function function = map.get(name);
    if (function == null) {
      throw new IllegalArgumentException("Function '" + name + "' doesn't exist.");
    }
    return function;
  }

  @Override
  public Set<String> names() {
    return ImmutableSet.copyOf(map.keySet());
  }
}
