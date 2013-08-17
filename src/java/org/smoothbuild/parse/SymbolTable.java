package org.smoothbuild.parse;

import java.util.Set;

import org.smoothbuild.registry.instantiate.Function;

public interface SymbolTable {

  public boolean containsFunction(String name);

  public Function getFunction(String name);

  public Set<String> names();
}
