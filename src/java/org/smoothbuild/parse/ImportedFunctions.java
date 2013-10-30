package org.smoothbuild.parse;

import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Name;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.ProvidedBy;

@Singleton
@ProvidedBy(ImportedFunctionsProvider.class)
public class ImportedFunctions implements SymbolTable {
  private final Map<String, Function> map = Maps.newHashMap();

  public void add(Function function) {
    Name functionName = function.name();
    String name = functionName.value();
    if (containsFunction(name)) {
      throw new IllegalArgumentException("Function with name " + functionName
          + " has already been imported.");
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
    return map.get(name);
  }

  @Override
  public Set<String> names() {
    return ImmutableSet.copyOf(map.keySet());
  }
}
